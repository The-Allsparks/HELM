package org.allsparks.helm.intent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.allsparks.helm.HelmConfig;
import org.allsparks.helm.clock.ManualClock;
import org.allsparks.helm.condition.ConditionValue;
import org.allsparks.helm.resource.Resource;
import org.allsparks.helm.sim.SimulatedTreeWalker;
import org.allsparks.helm.sim.SimulatedWorld;
import org.allsparks.helm.task.RetryPolicy;
import org.allsparks.helm.validate.PlanValidator;
import org.allsparks.helm.validate.ValidationReport;
import org.junit.jupiter.api.Test;

class IntentTreeBehaviorTest {
    private final ManualClock clock = new ManualClock();

    @Test
    void sequenceSucceedsOnlyWhenAllChildrenSucceed() {
        IntentTree tree = IntentTree.named("seq").sequence(
                IntentNode.action("one"),
                IntentNode.action("two"));
        SimulatedWorld world = new SimulatedWorld()
                .action("one", IntentStatus.SUCCEEDED)
                .action("two", IntentStatus.SUCCEEDED);
        assertEquals(IntentStatus.SUCCEEDED, new SimulatedTreeWalker(clock, world).tick(tree));

        SimulatedWorld failSecond = new SimulatedWorld()
                .action("one", IntentStatus.SUCCEEDED)
                .action("two", IntentStatus.FAILED);
        assertEquals(IntentStatus.FAILED, new SimulatedTreeWalker(clock, failSecond).tick(tree));
    }

    @Test
    void fallbackSelectsFirstSuccessAndExhaustionFails() {
        IntentTree tree = IntentTree.named("fb").fallback(
                IntentNode.action("primary"),
                IntentNode.action("ParkSafely"));
        SimulatedWorld world = new SimulatedWorld()
                .action("primary", IntentStatus.FAILED)
                .action("ParkSafely", IntentStatus.SUCCEEDED);
        assertEquals(IntentStatus.SUCCEEDED, new SimulatedTreeWalker(clock, world).tick(tree));

        SimulatedWorld none = new SimulatedWorld()
                .action("primary", IntentStatus.FAILED)
                .action("ParkSafely", IntentStatus.FAILED);
        assertEquals(IntentStatus.FAILED, new SimulatedTreeWalker(clock, none).tick(tree));
    }

    @Test
    void parallelFailsIfAnyChildFailsAndSucceedsWhenAllSucceed() {
        IntentTree tree = IntentTree.named("par").parallel(
                IntentNode.action("drive"),
                IntentNode.action("arm"));
        SimulatedWorld ok = new SimulatedWorld()
                .action("drive", IntentStatus.SUCCEEDED)
                .action("arm", IntentStatus.SUCCEEDED);
        assertEquals(IntentStatus.SUCCEEDED, new SimulatedTreeWalker(clock, ok).tick(tree));
        SimulatedWorld fail = new SimulatedWorld()
                .action("drive", IntentStatus.SUCCEEDED)
                .action("arm", IntentStatus.FAILED);
        assertEquals(IntentStatus.FAILED, new SimulatedTreeWalker(clock, fail).tick(tree));
    }

    @Test
    void timeoutAndBoundedRetryAreHonored() {
        IntentNode timed = IntentNode.timeout("limit", Duration.ofMillis(10), IntentNode.action("slow"));
        IntentTree timeoutTree = IntentTree.named("t").root(timed);
        SimulatedWorld running = new SimulatedWorld().action("slow", IntentStatus.RUNNING);
        SimulatedTreeWalker walker = new SimulatedTreeWalker(clock, running);
        assertEquals(IntentStatus.RUNNING, walker.tick(timeoutTree));
        clock.advanceMillis(11);
        assertEquals(IntentStatus.TIMED_OUT, walker.tick(timeoutTree));

        IntentNode retry = IntentNode.retry("retry", RetryPolicy.bounded(2, Duration.ofSeconds(1)),
                IntentNode.action("flaky"));
        IntentTree retryTree = IntentTree.named("r").root(IntentNode.fallback(retry, IntentNode.action("ParkSafely")));
        SimulatedWorld fail = new SimulatedWorld()
                .action("flaky", IntentStatus.FAILED)
                .action("ParkSafely", IntentStatus.SUCCEEDED);
        SimulatedTreeWalker retryWalker = new SimulatedTreeWalker(clock, fail);
        IntentStatus first = retryWalker.tick(retryTree);
        assertTrue(first == IntentStatus.RUNNING || first == IntentStatus.SUCCEEDED);
        IntentStatus second = retryWalker.tick(retryTree);
        assertTrue(second == IntentStatus.RUNNING || second == IntentStatus.SUCCEEDED);
        assertEquals(IntentStatus.SUCCEEDED, retryWalker.tick(retryTree));
    }

    @Test
    void cancelAndPreemptAreDistinctStatuses() {
        IntentTree tree = IntentTree.named("c").sequence(IntentNode.action("work"));
        SimulatedWorld world = new SimulatedWorld().action("work", IntentStatus.RUNNING);
        SimulatedTreeWalker cancelWalker = new SimulatedTreeWalker(clock, world);
        cancelWalker.tick(tree);
        cancelWalker.cancel();
        assertEquals(IntentStatus.CANCELLED, cancelWalker.tick(tree));

        SimulatedTreeWalker preemptWalker = new SimulatedTreeWalker(clock, world);
        preemptWalker.tick(tree);
        preemptWalker.preempt();
        assertEquals(IntentStatus.PREEMPTED, preemptWalker.tick(tree));
    }

    @Test
    void unknownConditionDoesNotSucceedAGuard() {
        IntentTree tree = IntentTree.named("g").root(
                IntentNode.guard("ready", IntentNode.condition("havePiece"), IntentNode.action("score")));
        SimulatedWorld world = new SimulatedWorld()
                .condition("havePiece", ConditionValue.UNKNOWN)
                .action("score", IntentStatus.SUCCEEDED);
        assertEquals(IntentStatus.UNAVAILABLE, new SimulatedTreeWalker(clock, world).tick(tree));
        world.condition("havePiece", ConditionValue.STALE);
        assertEquals(IntentStatus.BLOCKED, new SimulatedTreeWalker(clock, world).tick(tree));
    }

    @Test
    void validatorRejectsMissingTimeoutFallbackCyclesAndConflicts() {
        PlanValidator validator = new PlanValidator(HelmConfig.forTests(clock));
        IntentTree missingSafe = IntentTree.named("bad").sequence(IntentNode.action("ScorePreload"));
        ValidationReport report = validator.validate(missingSafe);
        assertFalse(report.isValid());

        IntentTree cyclic = IntentTree.named("cycle")
                .subtree("A", IntentNode.subtree("B"))
                .subtree("B", IntentNode.subtree("A"))
                .root(IntentNode.subtree("A"));
        assertTrue(validator.validate(cyclic).explanation().toLowerCase().contains("cyclic"));

        IntentNode left = IntentNode.builder("left", IntentNodeKind.ACTION)
                .resource(Resource.DRIVETRAIN)
                .build();
        IntentNode right = IntentNode.builder("right", IntentNodeKind.ACTION)
                .resource(Resource.DRIVETRAIN)
                .build();
        IntentTree conflict = IntentTree.named("conflict")
                .root(IntentNode.fallback(
                        IntentNode.parallel("both", left, right),
                        IntentNode.action("ParkSafely")));
        assertTrue(validator.validate(conflict).explanation().contains("exclusive resource"));
    }

    @Test
    void adapterFailureIsUnavailableNotSuccess() {
        IntentTree tree = IntentTree.named("adapter").sequence(
                IntentNode.action("missing-adapter"),
                IntentNode.action("ParkSafely"));
        SimulatedWorld world = new SimulatedWorld().action("ParkSafely", IntentStatus.SUCCEEDED);
        assertEquals(IntentStatus.UNAVAILABLE, new SimulatedTreeWalker(clock, world).tick(tree));
    }
}
