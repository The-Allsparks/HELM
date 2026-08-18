package org.allsparks.helm.examples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;

import org.allsparks.helm.Helm;
import org.allsparks.helm.HelmConfig;
import org.allsparks.helm.HelmFeatureFlags;
import org.allsparks.helm.HelmMode;
import org.allsparks.helm.capability.Capability;
import org.allsparks.helm.capability.CapabilityAvailability;
import org.allsparks.helm.clock.ManualClock;
import org.allsparks.helm.condition.Condition;
import org.allsparks.helm.condition.ConditionResult;
import org.allsparks.helm.condition.ConditionValue;
import org.allsparks.helm.confidence.Confidence;
import org.allsparks.helm.confidence.ConfidenceDimension;
import org.allsparks.helm.decision.DecisionRecord;
import org.allsparks.helm.goal.Goal;
import org.allsparks.helm.intent.IntentNode;
import org.allsparks.helm.intent.IntentNodeKind;
import org.allsparks.helm.intent.IntentTree;
import org.allsparks.helm.snapshot.HeldGamePiece;
import org.allsparks.helm.snapshot.PoseEstimate;
import org.allsparks.helm.snapshot.RemainingTime;
import org.allsparks.helm.snapshot.WorldSnapshot;
import org.allsparks.helm.task.Task;
import org.allsparks.helm.task.TaskEvaluation;
import org.allsparks.helm.task.TimeoutPolicy;
import org.allsparks.helm.validate.ValidationReport;
import org.allsparks.helm.validate.ValidationStatus;
import org.junit.jupiter.api.Test;

/**
 * Compilable Phase 0–2 desktop example. Not an OpMode. Never commands hardware.
 *
 * <p>Run: {@code ./gradlew test --tests org.allsparks.helm.examples.Phase0DescribeExampleTest}
 */
class Phase0DescribeExampleTest {
    private final ManualClock clock = new ManualClock();

    @Test
    void defaultInstallDoesNothingAndCannotCommandHardware() {
        Helm helm = Helm.create();
        assertEquals(HelmMode.OFF, helm.mode());
        assertFalse(helm.allowsPhysicalOutput());
    }

    @Test
    void describeTaskAndTreeThenEvaluateOnDesktop() {
        Goal scorePreload = Goal.named("ScorePreload");

        Task scoreTask = Task.builder("ScorePreload")
                .goal(scorePreload)
                .requires(Capability.DRIVE_TRANSLATION)
                .requires(Capability.LOW_SCORING)
                .timeout(Duration.ofSeconds(6))
                .fallback("ParkSafely")
                .completion(Condition.snapshotFact("preloadScored"))
                .precondition(Condition.snapshotFact("PreflightReady"))
                .build();

        IntentTree autonomous = IntentTree.named("SimpleAutonomous")
                .fallback(
                        IntentTree.sequence(
                                IntentTree.condition("PreflightReady"),
                                timedAction("ScorePreload"),
                                timedAction("AcquireNearestPiece")),
                        IntentNode.timeout(
                                "parkTimeout",
                                Duration.ofSeconds(3),
                                timedSafeTerminal("ParkSafely")));

        Helm helm = Helm.create(HelmConfig.builder()
                .mode(HelmMode.VALIDATE)
                .flags(HelmFeatureFlags.validate())
                .clock(clock)
                .build());

        assertFalse(helm.allowsPhysicalOutput());

        TaskEvaluation evaluation = helm.evaluate(scoreTask, desktopSnapshot());
        assertTrue(evaluation.isEligible());

        ValidationReport taskReport = helm.validate(scoreTask);
        assertEquals(ValidationStatus.VALID, taskReport.status());
        assertTrue(taskReport.isValid());

        ValidationReport treeReport = helm.validate(autonomous);
        assertEquals(ValidationStatus.VALID, treeReport.status());
        assertTrue(treeReport.isValid());

        DecisionRecord recommendation = helm.recommend(List.of(scoreTask), desktopSnapshot());
        assertFalse(recommendation.authoritative());
        assertEquals("SimpleAutonomous", autonomous.name());
    }

    /** Phase 2 rejects ACTION nodes that have no timeout. */
    private static IntentNode timedAction(String name) {
        return IntentNode.builder(name, IntentNodeKind.ACTION)
                .timeout(TimeoutPolicy.ofSeconds(3))
                .build();
    }

    private static IntentNode timedSafeTerminal(String name) {
        return IntentNode.builder(name, IntentNodeKind.ACTION)
                .safeTerminal(true)
                .timeout(TimeoutPolicy.ofSeconds(3))
                .build();
    }

    /**
     * Snapshots are built by the application (or a test). HELM does not sense the field.
     */
    private WorldSnapshot desktopSnapshot() {
        return WorldSnapshot.builder()
                .snapshotId("desktop-example")
                .timestampNanos(0L)
                .pose(PoseEstimate.builder()
                        .xInches(12)
                        .yInches(36)
                        .headingRadians(0)
                        .timestampNanos(0L)
                        .positionConfidence(Confidence.of(0.9d))
                        .headingConfidence(Confidence.of(0.9d))
                        .provider("desktop")
                        .build())
                .heldGamePiece(HeldGamePiece.empty(Confidence.of(0.8d), 0L))
                .capability(Capability.DRIVE_TRANSLATION, CapabilityAvailability.AVAILABLE, "beacon")
                .capability(Capability.LOW_SCORING, CapabilityAvailability.AVAILABLE, "mimic")
                .condition(ConditionResult.builder("PreflightReady")
                        .value(ConditionValue.TRUE)
                        .source("app")
                        .timestampNanos(0L)
                        .explanation("Preflight checks passed")
                        .build())
                .confidence(ConfidenceDimension.POSITION, Confidence.of(0.9d))
                .remainingAutonomous(RemainingTime.of(Duration.ofSeconds(25)))
                .build();
    }
}
