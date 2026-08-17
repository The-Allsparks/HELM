package org.allsparks.helm.validate;

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
import org.allsparks.helm.task.Task;
import org.junit.jupiter.api.Test;

class SkippedValidationTest {
    private final ManualClock clock = new ManualClock();

    @Test
    void offModeDoesNotReportValidPlan() {
        Helm helm = Helm.create();
        Task task = Task.builder("ParkSafely")
                .timeout(Duration.ofSeconds(3))
                .fallback("HoldSafe")
                .completion(Condition.snapshotFact("parked"))
                .build();

        ValidationReport report = helm.validate(task);

        assertEquals(ValidationStatus.NOT_RUN, report.status());
        assertFalse(report.wasValidated());
        assertFalse(report.isValid());
        assertTrue(report.findings().isEmpty());
        assertTrue(report.explanation().contains("was not validated"));
        assertFalse(report.explanation().contains("is valid"));
    }

    @Test
    void validateModeWithoutPhase2FlagDoesNotReportValidPlan() {
        Helm helm = Helm.create(HelmConfig.builder()
                .mode(HelmMode.VALIDATE)
                .clock(clock)
                .build());
        Task task = Task.builder("ParkSafely")
                .timeout(Duration.ofSeconds(3))
                .fallback("HoldSafe")
                .completion(Condition.snapshotFact("parked"))
                .build();

        ValidationReport report = helm.validate(task);

        assertEquals(ValidationStatus.NOT_RUN, report.status());
        assertFalse(report.isValid());
        assertTrue(report.explanation().contains("Phase 2 validation is not enabled"));
        assertFalse(report.explanation().contains("is valid"));
    }

    @Test
    void validateModeWithFlagsReportsValidForCompleteTask() {
        Helm helm = Helm.create(HelmConfig.builder()
                .mode(HelmMode.VALIDATE)
                .flags(HelmFeatureFlags.validate())
                .clock(clock)
                .build());
        Task task = Task.builder("ParkSafely")
                .timeout(Duration.ofSeconds(3))
                .fallback("HoldSafe")
                .completion(Condition.snapshotFact("parked"))
                .build();

        ValidationReport report = helm.validate(task);

        assertEquals(ValidationStatus.VALID, report.status());
        assertTrue(report.wasValidated());
        assertTrue(report.isValid());
        assertTrue(report.explanation().contains("is valid"));
    }

    @Test
    void observeModeDoesNotReportValidPlan() {
        Helm helm = Helm.create(HelmConfig.builder()
                .mode(HelmMode.OBSERVE)
                .flags(HelmFeatureFlags.validate())
                .clock(clock)
                .build());
        Task task = Task.builder("ParkSafely").build();

        ValidationReport report = helm.validate(task);

        assertEquals(ValidationStatus.NOT_RUN, report.status());
        assertFalse(report.isValid());
        assertTrue(report.explanation().contains("does not allow validation"));
    }
}
