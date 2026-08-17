package org.allsparks.helm.observe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Optional;

import org.allsparks.helm.Helm;
import org.allsparks.helm.HelmConfig;
import org.allsparks.helm.HelmFeatureFlags;
import org.allsparks.helm.HelmMode;
import org.allsparks.helm.clock.ManualClock;
import org.allsparks.helm.condition.Condition;
import org.allsparks.helm.outcome.FailureReason;
import org.allsparks.helm.outcome.Outcome;
import org.allsparks.helm.snapshot.WorldSnapshot;
import org.allsparks.helm.task.Task;
import org.allsparks.helm.trace.RecordingTraceSink;
import org.allsparks.helm.validate.ValidationReport;
import org.junit.jupiter.api.Test;

class ObserveAndValidateTest {
    private final ManualClock clock = new ManualClock();

    @Test
    void observeRecordsDurationAndFailureReasonWhenEnabled() {
        RecordingTraceSink trace = new RecordingTraceSink();
        Helm helm = Helm.create(HelmConfig.builder()
                .mode(HelmMode.OBSERVE)
                .flags(HelmFeatureFlags.observe())
                .clock(clock)
                .traceSink(trace)
                .build());
        clock.advanceMillis(250);
        Optional<ObservedEvent> event = helm.observe(
                StatedIntent.named("ScorePreload"),
                Outcome.FAILED,
                FailureReason.TIMEOUT,
                0L,
                WorldSnapshot.builder().snapshotId("obs").timestampNanos(0L).build());
        assertTrue(event.isPresent());
        assertEquals(Duration.ofMillis(250), event.get().duration());
        assertEquals(FailureReason.TIMEOUT, event.get().failureReason());
        assertEquals(1, helm.history().size());
        assertTrue(trace.events().stream().anyMatch(e -> e.type().equals("stated-intent")));
    }

    @Test
    void observeIsIgnoredWhenOff() {
        Helm helm = Helm.create(HelmConfig.defaults());
        assertTrue(helm.observe(
                StatedIntent.named("ScorePreload"),
                Outcome.SUCCEEDED,
                FailureReason.NONE,
                0L,
                null).isEmpty());
        assertEquals(0, helm.history().size());
    }

    @Test
    void validateRejectsIncompleteTasksAndDoesNotRewriteThem() {
        Helm helm = Helm.create(HelmConfig.forTests(clock));
        Task incomplete = Task.builder("ScorePreload").build();
        ValidationReport report = helm.validate(incomplete);
        assertFalse(report.isValid());
        assertTrue(report.explanation().contains("not safe to run"));
        Task stillSame = incomplete;
        assertTrue(stillSame.timeout().isEmpty());
    }

    @Test
    void validateRequiresCompletionTimeoutAndFallback() {
        Helm helm = Helm.create(HelmConfig.forTests(clock));
        Task complete = Task.builder("ParkSafely")
                .timeout(Duration.ofSeconds(3))
                .fallback("HoldSafe")
                .completion(Condition.snapshotFact("parked"))
                .build();
        assertTrue(helm.validate(complete).isValid());
    }
}
