package org.allsparks.helm.task;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

/**
 * Explicit timeout. Missing timeouts fail validation.
 */
public final class TimeoutPolicy {
    private final Duration duration;

    private TimeoutPolicy(Duration duration) {
        this.duration = Objects.requireNonNull(duration, "duration");
        if (duration.isNegative() || duration.isZero()) {
            throw new IllegalArgumentException("Timeout must be positive");
        }
    }

    public static TimeoutPolicy of(Duration duration) {
        return new TimeoutPolicy(duration);
    }

    public static TimeoutPolicy ofSeconds(long seconds) {
        return of(Duration.ofSeconds(seconds));
    }

    public Duration duration() {
        return duration;
    }

    public Optional<Duration> asOptional() {
        return Optional.of(duration);
    }
}
