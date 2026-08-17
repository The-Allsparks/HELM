package org.allsparks.helm.observe;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

import org.allsparks.helm.outcome.FailureReason;
import org.allsparks.helm.outcome.Outcome;

/**
 * One observed start/finish of a stated intent.
 */
public final class ObservedEvent {
    private final long sequence;
    private final StatedIntent intent;
    private final Outcome outcome;
    private final FailureReason failureReason;
    private final long startedAtNanos;
    private final long finishedAtNanos;
    private final String snapshotId;

    public ObservedEvent(
            long sequence,
            StatedIntent intent,
            Outcome outcome,
            FailureReason failureReason,
            long startedAtNanos,
            long finishedAtNanos,
            String snapshotId) {
        this.sequence = sequence;
        this.intent = Objects.requireNonNull(intent, "intent");
        this.outcome = Objects.requireNonNull(outcome, "outcome");
        this.failureReason = failureReason == null ? FailureReason.NONE : failureReason;
        this.startedAtNanos = startedAtNanos;
        this.finishedAtNanos = finishedAtNanos;
        this.snapshotId = snapshotId == null ? "" : snapshotId;
    }

    public long sequence() {
        return sequence;
    }

    public StatedIntent intent() {
        return intent;
    }

    public Outcome outcome() {
        return outcome;
    }

    public FailureReason failureReason() {
        return failureReason;
    }

    public long startedAtNanos() {
        return startedAtNanos;
    }

    public long finishedAtNanos() {
        return finishedAtNanos;
    }

    public Duration duration() {
        return Duration.ofNanos(Math.max(0L, finishedAtNanos - startedAtNanos));
    }

    public String snapshotId() {
        return snapshotId;
    }

    public Optional<String> explanation() {
        if (outcome == Outcome.SUCCEEDED) {
            return Optional.of(intent.name() + " succeeded in " + duration().toMillis() + " ms");
        }
        return Optional.of(intent.name() + " ended " + outcome + " because " + failureReason.explanation());
    }
}
