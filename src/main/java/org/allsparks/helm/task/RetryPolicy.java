package org.allsparks.helm.task;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

/**
 * Bounded retry policy. Unbounded retries are prohibited.
 */
public final class RetryPolicy {
    private final int maxAttempts;
    private final Duration maxDuration;
    private final boolean requireImprovementEvidence;

    private RetryPolicy(int maxAttempts, Duration maxDuration, boolean requireImprovementEvidence) {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("Retry maxAttempts must be at least 1");
        }
        this.maxAttempts = maxAttempts;
        this.maxDuration = Objects.requireNonNull(maxDuration, "maxDuration");
        if (maxDuration.isNegative() || maxDuration.isZero()) {
            throw new IllegalArgumentException("Retry maxDuration must be positive");
        }
        this.requireImprovementEvidence = requireImprovementEvidence;
    }

    public static RetryPolicy none() {
        return new RetryPolicy(1, Duration.ofMillis(1), false);
    }

    public static RetryPolicy bounded(int maxAttempts, Duration maxDuration) {
        return new RetryPolicy(maxAttempts, maxDuration, true);
    }

    public int maxAttempts() {
        return maxAttempts;
    }

    public Duration maxDuration() {
        return maxDuration;
    }

    public boolean requireImprovementEvidence() {
        return requireImprovementEvidence;
    }

    public boolean isRetrying() {
        return maxAttempts > 1;
    }

    public Optional<String> unboundedReason() {
        return Optional.empty();
    }
}
