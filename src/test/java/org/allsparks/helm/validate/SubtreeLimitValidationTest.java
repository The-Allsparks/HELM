package org.allsparks.helm.validate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.allsparks.helm.HelmConfig;
import org.allsparks.helm.HelmFeatureFlags;
import org.allsparks.helm.HelmMode;
import org.allsparks.helm.clock.ManualClock;
import org.allsparks.helm.intent.IntentNode;
import org.allsparks.helm.intent.IntentNodeKind;
import org.allsparks.helm.intent.IntentTree;
import org.allsparks.helm.resource.Resource;
import org.allsparks.helm.task.TimeoutPolicy;
import org.junit.jupiter.api.Test;

class SubtreeLimitValidationTest {
    private final ManualClock clock = new ManualClock();
    private final PlanValidator defaultValidator = new PlanValidator(HelmConfig.forTests(clock));

    @Test
    void expandedNodeCountExceedingMaxTreeNodesIsError() {
        PlanValidator validator = validatorWithLimits(1, 16);
        IntentTree tree = IntentTree.named("oversize-nodes")
                .subtree("payload", timed("ScorePreload"))
                .root(IntentNode.subtree("payload"));
        ValidationReport report = validator.validate(tree);
        assertFalse(report.isValid());
        assertTrue(report.explanation().contains("max nodes"));
    }

    @Test
    void expandedDepthExceedingMaxTreeDepthIsError() {
        PlanValidator validator = validatorWithLimits(64, 1);
        IntentTree tree = IntentTree.named("oversize-depth")
                .subtree("payload", timed("ScorePreload"))
                .root(IntentNode.subtree("payload"));
        ValidationReport report = validator.validate(tree);
        assertFalse(report.isValid());
        assertTrue(report.explanation().contains("max depth"));
    }

    @Test
    void parallelExclusiveDrivetrainInsideNamedSubtreeIsError() {
        IntentNode left = IntentNode.builder("left", IntentNodeKind.ACTION)
                .timeout(TimeoutPolicy.ofSeconds(2))
                .resource(Resource.DRIVETRAIN)
                .build();
        IntentNode nestedDrive = IntentNode.builder("nestedDrive", IntentNodeKind.ACTION)
                .timeout(TimeoutPolicy.ofSeconds(2))
                .resource(Resource.DRIVETRAIN)
                .build();
        IntentTree tree = IntentTree.named("conflict")
                .subtree("inside", nestedDrive)
                .root(IntentNode.fallback(
                        IntentNode.parallel("both", left, IntentNode.subtree("inside")),
                        timeoutWrappedSafeTerminal("ParkSafely")));
        ValidationReport report = defaultValidator.validate(tree);
        assertFalse(report.isValid());
        assertTrue(report.explanation().contains("exclusive resource"));
    }

    @Test
    void cyclicSubtreeIsStillReported() {
        IntentTree cyclic = IntentTree.named("cycle")
                .subtree("A", IntentNode.subtree("B"))
                .subtree("B", IntentNode.subtree("A"))
                .root(IntentNode.subtree("A"));
        ValidationReport report = defaultValidator.validate(cyclic);
        assertFalse(report.isValid());
        assertTrue(report.explanation().toLowerCase().contains("cyclic"));
    }

    @Test
    void unreachableSubtreeIsWarningAndDoesNotCountTowardLimits() {
        PlanValidator validator = validatorWithLimits(6, 8);
        IntentTree tree = IntentTree.named("orphan")
                .subtree("unused", IntentNode.sequence(
                        timed("a"), timed("b"), timed("c"), timed("d"), timed("e"), timed("f")))
                .root(IntentNode.fallback(
                        timed("ScorePreload"),
                        timeoutWrappedSafeTerminal("ParkSafely")));
        ValidationReport report = validator.validate(tree);
        assertTrue(report.isValid());
        assertTrue(report.errors().isEmpty());
        assertTrue(report.findings().stream().anyMatch(finding ->
                finding.severity() == ValidationSeverity.WARNING
                        && finding.message().contains("not reachable")));
    }

    @Test
    void smallExpandedTreeUnderLimitsCanBeValid() {
        IntentTree tree = IntentTree.named("ok")
                .subtree("score", timed("ScorePreload"))
                .root(IntentNode.fallback(
                        IntentNode.subtree("score"),
                        timeoutWrappedSafeTerminal("ParkSafely")));
        assertTrue(defaultValidator.validate(tree).isValid());
    }

    private PlanValidator validatorWithLimits(int maxTreeNodes, int maxTreeDepth) {
        return new PlanValidator(HelmConfig.builder()
                .mode(HelmMode.VALIDATE)
                .flags(HelmFeatureFlags.validate())
                .clock(clock)
                .maxTreeNodes(maxTreeNodes)
                .maxTreeDepth(maxTreeDepth)
                .build());
    }

    private static IntentNode timed(String name) {
        return IntentNode.builder(name, IntentNodeKind.ACTION)
                .timeout(TimeoutPolicy.ofSeconds(2))
                .build();
    }

    private static IntentNode timedSafeTerminal(String name) {
        return IntentNode.builder(name, IntentNodeKind.ACTION)
                .safeTerminal(true)
                .timeout(TimeoutPolicy.ofSeconds(2))
                .build();
    }

    private static IntentNode timeoutWrappedSafeTerminal(String name) {
        return IntentNode.timeout("parkTimeout", Duration.ofSeconds(2), timedSafeTerminal(name));
    }
}
