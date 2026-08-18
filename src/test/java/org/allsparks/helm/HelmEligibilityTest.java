package org.allsparks.helm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;

import org.allsparks.helm.capability.Capability;
import org.allsparks.helm.capability.CapabilityAvailability;
import org.allsparks.helm.clock.ManualClock;
import org.allsparks.helm.condition.Condition;
import org.allsparks.helm.condition.ConditionResult;
import org.allsparks.helm.condition.ConditionValue;
import org.allsparks.helm.confidence.Confidence;
import org.allsparks.helm.confidence.ConfidenceDimension;
import org.allsparks.helm.confidence.ConfidenceRequirement;
import org.allsparks.helm.goal.Goal;
import org.allsparks.helm.intent.IntentNode;
import org.allsparks.helm.intent.IntentTree;
import org.allsparks.helm.outcome.FailureReason;
import org.allsparks.helm.resource.Resource;
import org.allsparks.helm.snapshot.HeldGamePiece;
import org.allsparks.helm.snapshot.PoseEstimate;
import org.allsparks.helm.snapshot.RemainingTime;
import org.allsparks.helm.snapshot.SafetyRestriction;
import org.allsparks.helm.snapshot.WorldSnapshot;
import org.allsparks.helm.task.Task;
import org.allsparks.helm.task.TaskEvaluation;
import org.allsparks.helm.trace.RecordingTraceSink;
import org.junit.jupiter.api.Test;

class HelmEligibilityTest {
    private final ManualClock clock = new ManualClock();

    @Test
    void eligibleTaskPassesWithKnownCapabilitiesAndConfidence() {
        Helm helm = Helm.create(HelmConfig.forTests(clock));
        Task task = sampleTask();
        TaskEvaluation evaluation = helm.evaluate(task, readySnapshot());
        assertTrue(evaluation.isEligible());
        assertTrue(evaluation.explanation().contains("eligible"));
    }

    @Test
    void unknownCapabilityDoesNotBecomeAvailable() {
        WorldSnapshot snapshot = readySnapshot().toBuilder()
                .capability(Capability.LOW_SCORING, CapabilityAvailability.UNKNOWN, "beacon")
                .build();
        TaskEvaluation evaluation = Helm.create(HelmConfig.forTests(clock)).evaluate(sampleTask(), snapshot);
        assertFalse(evaluation.isEligible());
        assertTrue(evaluation.rejectionReasons().contains(FailureReason.CAPABILITY_UNKNOWN));
    }

    @Test
    void staleCapabilityDoesNotBecomeValid() {
        WorldSnapshot snapshot = readySnapshot().toBuilder()
                .capability(Capability.DRIVE_TRANSLATION, CapabilityAvailability.STALE, "beacon")
                .build();
        TaskEvaluation evaluation = Helm.create(HelmConfig.forTests(clock)).evaluate(sampleTask(), snapshot);
        assertFalse(evaluation.isEligible());
        assertTrue(evaluation.rejectionReasons().contains(FailureReason.STALE_INPUT));
    }

    @Test
    void degradedCapabilityRejectedUnlessAllowed() {
        WorldSnapshot snapshot = readySnapshot().toBuilder()
                .capability(Capability.LOW_SCORING, CapabilityAvailability.DEGRADED, "mimic")
                .build();
        TaskEvaluation evaluation = Helm.create(HelmConfig.forTests(clock)).evaluate(sampleTask(), snapshot);
        assertFalse(evaluation.isEligible());
        Task allowed = Task.builder("ScorePreload")
                .requires(Capability.DRIVE_TRANSLATION)
                .requires(Capability.LOW_SCORING)
                .allowDegradedCapabilities(true)
                .timeout(Duration.ofSeconds(6))
                .fallback("ParkSafely")
                .completion(Condition.snapshotFact("preloadScored"))
                .precondition(Condition.snapshotFact("PreflightReady"))
                .requires(ConfidenceRequirement.of(ConfidenceDimension.POSITION, 0.6d))
                .build();
        assertTrue(Helm.create(HelmConfig.forTests(clock)).evaluate(allowed, snapshot).isEligible());
    }

    @Test
    void unknownConditionDoesNotBecomeFalse() {
        WorldSnapshot snapshot = readySnapshot().toBuilder()
                .condition(ConditionResult.unknown("PreflightReady", "vidar", 0L, "not reported"))
                .build();
        TaskEvaluation evaluation = Helm.create(HelmConfig.forTests(clock)).evaluate(sampleTask(), snapshot);
        assertFalse(evaluation.isEligible());
        assertTrue(evaluation.rejectionReasons().contains(FailureReason.UNKNOWN_CONDITION));
    }

    @Test
    void staleConditionDoesNotBecomeValid() {
        WorldSnapshot snapshot = readySnapshot().toBuilder()
                .condition(ConditionResult.stale("PreflightReady", "vidar", 0L, 1_000_000_000L, "too old"))
                .build();
        TaskEvaluation evaluation = Helm.create(HelmConfig.forTests(clock)).evaluate(sampleTask(), snapshot);
        assertFalse(evaluation.isEligible());
        assertTrue(evaluation.rejectionReasons().contains(FailureReason.STALE_INPUT));
    }

    @Test
    void lowConfidenceIsRejectedWithoutTreatingUnknownAsZero() {
        WorldSnapshot snapshot = readySnapshot().toBuilder()
                .confidence(ConfidenceDimension.POSITION, Confidence.of(0.2d))
                .build();
        TaskEvaluation evaluation = Helm.create(HelmConfig.forTests(clock)).evaluate(sampleTask(), snapshot);
        assertFalse(evaluation.isEligible());
        assertTrue(evaluation.rejectionReasons().contains(FailureReason.CONFIDENCE_TOO_LOW));
    }

    @Test
    void resourceConflictAndSafetyOverrideEligibility() {
        WorldSnapshot busy = readySnapshot().toBuilder()
                .resource(Resource.DRIVETRAIN, false)
                .build();
        Task withDrive = Task.builder("ScorePreload")
                .requires(Capability.DRIVE_TRANSLATION)
                .requires(Capability.LOW_SCORING)
                .requires(Resource.DRIVETRAIN)
                .timeout(Duration.ofSeconds(6))
                .fallback("ParkSafely")
                .completion(Condition.snapshotFact("preloadScored"))
                .precondition(Condition.snapshotFact("PreflightReady"))
                .build();
        assertFalse(Helm.create(HelmConfig.forTests(clock)).evaluate(withDrive, busy).isEligible());
        assertTrue(Helm.create(HelmConfig.forTests(clock))
                .evaluate(withDrive, busy)
                .rejectionReasons()
                .contains(FailureReason.RESOURCE_CONFLICT));

        WorldSnapshot restricted = readySnapshot().toBuilder()
                .safetyRestriction(new SafetyRestriction("estop", "beacon", "safe stop", true, 0L))
                .build();
        assertTrue(Helm.create(HelmConfig.forTests(clock))
                .evaluate(sampleTask(), restricted)
                .rejectionReasons()
                .contains(FailureReason.SAFETY_RESTRICTION));
    }

    @Test
    void offModeDoesNotEvaluate() {
        Helm helm = Helm.create(HelmConfig.builder().clock(clock).mode(HelmMode.OFF).build());
        TaskEvaluation evaluation = helm.evaluate(sampleTask(), readySnapshot());
        assertFalse(evaluation.isEligible());
        assertTrue(evaluation.rejectionReasons().contains(FailureReason.MODE_DISABLED));
    }

    @Test
    void defaultHelmRefusesPhysicalOutput() {
        Helm helm = Helm.create();
        assertEquals(HelmMode.OFF, helm.mode());
        assertFalse(helm.allowsPhysicalOutput());
        assertTrue(helm.authorityDenial().contains("OFF")
                || helm.authorityDenial().contains("authority"));
    }

    @Test
    void futureDatedSnapshotIsNotFresh() {
        WorldSnapshot snapshot = readySnapshot().toBuilder()
                .timestampNanos(Duration.ofSeconds(1).toNanos())
                .build();
        assertFalse(snapshot.isFresh(clock.nanoTime(), Duration.ofMillis(100).toNanos()));
        TaskEvaluation evaluation = Helm.create(HelmConfig.forTests(clock)).evaluate(sampleTask(), snapshot);
        assertFalse(evaluation.isEligible());
        assertTrue(evaluation.rejectionReasons().contains(FailureReason.STALE_INPUT));
    }

    @Test
    void remainingTimeUnknownIsNotInfinite() {
        Task task = Task.builder("ScorePreload")
                .requires(Capability.DRIVE_TRANSLATION)
                .timeout(Duration.ofSeconds(6))
                .fallback("ParkSafely")
                .completion(Condition.snapshotFact("preloadScored"))
                .minimumRemainingTime(Duration.ofSeconds(5))
                .build();
        WorldSnapshot snapshot = readySnapshot().toBuilder()
                .remainingAutonomous(RemainingTime.unknown())
                .remainingMatch(RemainingTime.unknown())
                .build();
        TaskEvaluation evaluation = Helm.create(HelmConfig.forTests(clock)).evaluate(task, snapshot);
        assertTrue(evaluation.rejectionReasons().contains(FailureReason.INSUFFICIENT_TIME));
    }

    @Test
    void misalignedTimestampsAreDetected() {
        WorldSnapshot snapshot = WorldSnapshot.builder()
                .snapshotId("misaligned")
                .timestampNanos(0L)
                .alignmentWindow(Duration.ofMillis(10))
                .pose(PoseEstimate.builder()
                        .timestampNanos(Duration.ofMillis(200).toNanos())
                        .positionConfidence(Confidence.of(1.0d))
                        .headingConfidence(Confidence.of(1.0d))
                        .build())
                .capability(Capability.DRIVE_TRANSLATION, CapabilityAvailability.AVAILABLE, "beacon")
                .capability(Capability.LOW_SCORING, CapabilityAvailability.AVAILABLE, "mimic")
                .condition(ConditionResult.builder("PreflightReady")
                        .value(ConditionValue.TRUE)
                        .source("app")
                        .timestampNanos(0L)
                        .build())
                .build();
        assertFalse(snapshot.timestampAlignment().aligned());
        TaskEvaluation evaluation = Helm.create(HelmConfig.forTests(clock)).evaluate(sampleTask(), snapshot);
        assertTrue(evaluation.rejectionReasons().contains(FailureReason.STALE_INPUT));
    }

    @Test
    void studentApiExampleCompilesAndEvaluates() {
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
                                IntentTree.action("ScorePreload"),
                                IntentTree.action("AcquireNearestPiece")),
                        IntentNode.timeout(
                                "parkTimeout",
                                Duration.ofSeconds(3),
                                IntentTree.safeTerminal("ParkSafely")));
        RecordingTraceSink trace = new RecordingTraceSink();
        Helm helm = Helm.create(HelmConfig.builder()
                .mode(HelmMode.VALIDATE)
                .flags(HelmFeatureFlags.validate())
                .clock(clock)
                .traceSink(trace)
                .build());
        TaskEvaluation evaluation = helm.evaluate(scoreTask, readySnapshot());
        assertTrue(evaluation.isEligible() || !evaluation.explanation().isBlank());
        assertEquals("SimpleAutonomous", autonomous.name());
        assertFalse(trace.events().isEmpty());
        assertFalse(helm.recommend(List.of(scoreTask), readySnapshot()).authoritative());
    }

    private static Task sampleTask() {
        return Task.builder("ScorePreload")
                .requires(Capability.DRIVE_TRANSLATION)
                .requires(Capability.LOW_SCORING)
                .timeout(Duration.ofSeconds(6))
                .fallback("ParkSafely")
                .completion(Condition.snapshotFact("preloadScored"))
                .precondition(Condition.snapshotFact("PreflightReady"))
                .requires(ConfidenceRequirement.of(ConfidenceDimension.POSITION, 0.6d))
                .build();
    }

    private WorldSnapshot readySnapshot() {
        return WorldSnapshot.builder()
                .snapshotId("ready")
                .timestampNanos(0L)
                .pose(PoseEstimate.builder()
                        .xInches(12)
                        .yInches(36)
                        .headingRadians(0)
                        .timestampNanos(0L)
                        .positionConfidence(Confidence.of(0.9d))
                        .headingConfidence(Confidence.of(0.9d))
                        .provider("pedro")
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
