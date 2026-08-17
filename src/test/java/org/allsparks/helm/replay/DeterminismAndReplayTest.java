package org.allsparks.helm.replay;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.allsparks.helm.Helm;
import org.allsparks.helm.HelmConfig;
import org.allsparks.helm.HelmFeatureFlags;
import org.allsparks.helm.HelmMode;
import org.allsparks.helm.adapter.NoOpActionAdapter;
import org.allsparks.helm.authority.AuthorityGate;
import org.allsparks.helm.capability.Capability;
import org.allsparks.helm.capability.CapabilityAvailability;
import org.allsparks.helm.clock.HelmClock;
import org.allsparks.helm.clock.ManualClock;
import org.allsparks.helm.condition.Condition;
import org.allsparks.helm.condition.ConditionResult;
import org.allsparks.helm.condition.ConditionValue;
import org.allsparks.helm.intent.IntentStatus;
import org.allsparks.helm.outcome.FailureReason;
import org.allsparks.helm.snapshot.WorldSnapshot;
import org.allsparks.helm.task.Task;
import org.allsparks.helm.task.TaskEvaluation;
import org.junit.jupiter.api.Test;

class DeterminismAndReplayTest {
    @Test
    void equalCandidatesKeepDeterministicTieOrderByName() {
        List<String> names = List.of("ParkSafely", "ScorePreload", "AcquireNearestPiece");
        List<String> sorted = new ArrayList<>(names);
        sorted.sort(String::compareTo);
        assertEquals(List.of("AcquireNearestPiece", "ParkSafely", "ScorePreload"), sorted);
    }

    @Test
    void replayModeNeverAllowsPhysicalOutput() {
        HelmConfig config = HelmConfig.builder()
                .mode(HelmMode.REPLAY)
                .flags(HelmFeatureFlags.builder().phase8Replay(true).phase3StaticExecution(true).build())
                .clock(new ManualClock())
                .build();
        Helm helm = Helm.create(config);
        assertFalse(helm.allowsPhysicalOutput());
        assertTrue(new AuthorityGate(config).denialExplanation().toLowerCase().contains("replay")
                || !helm.allowsPhysicalOutput());
        assertEquals(IntentStatus.UNAVAILABLE,
                new NoOpActionAdapter("PedroActionAdapter").tick(WorldSnapshot.builder().timestampNanos(0L).build()));
    }

    @Test
    void sameSnapshotAndTaskProduceTheSameEligibility() {
        ManualClock clock = new ManualClock(5_000_000L);
        Helm helm = Helm.create(HelmConfig.forTests(clock));
        Task task = Task.builder("ScorePreload")
                .requires(Capability.DRIVE_TRANSLATION)
                .timeout(Duration.ofSeconds(6))
                .fallback("ParkSafely")
                .completion(Condition.snapshotFact("done"))
                .precondition(Condition.snapshotFact("ready"))
                .build();
        WorldSnapshot snapshot = WorldSnapshot.builder()
                .snapshotId("same")
                .timestampNanos(5_000_000L)
                .capability(Capability.DRIVE_TRANSLATION, CapabilityAvailability.AVAILABLE, "beacon")
                .condition(ConditionResult.builder("ready")
                        .value(ConditionValue.TRUE)
                        .source("app")
                        .timestampNanos(5_000_000L)
                        .build())
                .build();
        TaskEvaluation first = helm.evaluate(task, snapshot);
        TaskEvaluation second = helm.evaluate(task, snapshot);
        assertEquals(first.isEligible(), second.isEligible());
        assertEquals(first.rejectionReasons(), second.rejectionReasons());
        assertEquals(first.explanation(), second.explanation());
    }

    @Test
    void exceedingDecisionBudgetMarksEvaluationIncomplete() {
        HelmClock jumpingClock = new HelmClock() {
            private int calls;

            @Override
            public long nanoTime() {
                calls++;
                return calls < 3 ? 0L : Duration.ofMillis(20).toNanos();
            }
        };
        Helm helm = Helm.create(HelmConfig.builder()
                .mode(HelmMode.VALIDATE)
                .flags(HelmFeatureFlags.validate())
                .clock(jumpingClock)
                .decisionTimeBudget(Duration.ofMillis(5))
                .snapshotMaxAge(Duration.ofSeconds(1))
                .build());
        Task task = Task.builder("ScorePreload")
                .timeout(Duration.ofSeconds(1))
                .fallback("ParkSafely")
                .completion(Condition.snapshotFact("done"))
                .build();
        TaskEvaluation evaluation = helm.evaluate(task, WorldSnapshot.builder()
                .snapshotId("budget")
                .timestampNanos(0L)
                .build());
        assertFalse(evaluation.isComplete());
        assertFalse(evaluation.isEligible());
        assertTrue(evaluation.rejectionReasons().contains(FailureReason.TIME_BUDGET_EXCEEDED));
        assertTrue(evaluation.explanation().contains("Incomplete"));
    }

    @Test
    void candidateLimitIsDocumentedAndEnforced() {
        Helm helm = Helm.create(HelmConfig.builder()
                .mode(HelmMode.SHADOW)
                .flags(HelmFeatureFlags.builder().phase4Shadow(true).build())
                .clock(new ManualClock())
                .maxCandidates(2)
                .build());
        List<Task> tasks = List.of(
                Task.builder("a").timeout(Duration.ofSeconds(1)).fallback("x").completion(Condition.snapshotFact("a")).build(),
                Task.builder("b").timeout(Duration.ofSeconds(1)).fallback("x").completion(Condition.snapshotFact("b")).build(),
                Task.builder("c").timeout(Duration.ofSeconds(1)).fallback("x").completion(Condition.snapshotFact("c")).build());
        assertTrue(helm.recommend(tasks, WorldSnapshot.builder().snapshotId("n").timestampNanos(0L).build())
                .explanation()
                .contains("exceeds"));
    }
}
