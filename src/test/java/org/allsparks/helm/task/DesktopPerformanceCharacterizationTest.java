package org.allsparks.helm.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.allsparks.helm.Helm;
import org.allsparks.helm.HelmConfig;
import org.allsparks.helm.HelmFeatureFlags;
import org.allsparks.helm.HelmMode;
import org.allsparks.helm.clock.ManualClock;
import org.allsparks.helm.condition.Condition;
import org.allsparks.helm.outcome.FailureReason;
import org.allsparks.helm.snapshot.WorldSnapshot;
import org.junit.jupiter.api.Test;

/**
 * Desktop characterization of decision-time accounting. This is not a Control Hub
 * benchmark and must not be cited as hardware proof.
 *
 * <p>{@link ManualClock} does not advance unless the test moves it, so
 * {@link TaskEvaluation#evaluationNanos()} is zero on the happy path. The 5 ms
 * budget is a policy enforced against the configured {@code HelmClock}, not a
 * measured device latency. Wall-clock assertions are intentionally absent so CI
 * is not flaky.
 *
 * @see org.allsparks.helm.replay.DeterminismAndReplayTest#exceedingDecisionBudgetMarksEvaluationIncomplete()
 */
class DesktopPerformanceCharacterizationTest {
    @Test
    void manualClockRecordsZeroEvaluationNanosWithoutProvingDeviceLatency() {
        ManualClock clock = new ManualClock();
        Helm helm = Helm.create(HelmConfig.builder()
                .mode(HelmMode.VALIDATE)
                .flags(HelmFeatureFlags.validate())
                .clock(clock)
                .decisionTimeBudget(Duration.ofMillis(5))
                .snapshotMaxAge(Duration.ofSeconds(1))
                .build());
        TaskEvaluation evaluation = helm.evaluate(sampleTask(), desktopSnapshot());
        assertEquals(0L, evaluation.evaluationNanos(),
                "ManualClock is frozen; zero evaluationNanos is not a 5 ms Control Hub measurement");
        assertTrue(evaluation.isComplete());
        assertFalse(evaluation.rejectionReasons().contains(FailureReason.TIME_BUDGET_EXCEEDED));
    }

    private static Task sampleTask() {
        return Task.builder("ScorePreload")
                .timeout(Duration.ofSeconds(1))
                .fallback("ParkSafely")
                .completion(Condition.snapshotFact("done"))
                .build();
    }

    private static WorldSnapshot desktopSnapshot() {
        return WorldSnapshot.builder()
                .snapshotId("desktop-perf")
                .timestampNanos(0L)
                .build();
    }
}
