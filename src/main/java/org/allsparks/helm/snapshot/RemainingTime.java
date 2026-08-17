package org.allsparks.helm.snapshot;

import java.time.Duration;
import java.util.Optional;

/**
 * Remaining match or autonomous time as reported by the robot application.
 * Unknown remaining time is not treated as infinite.
 */
public final class RemainingTime {
    private final boolean known;
    private final Duration remaining;

    private RemainingTime(boolean known, Duration remaining) {
        this.known = known;
        this.remaining = remaining;
    }

    public static RemainingTime unknown() {
        return new RemainingTime(false, Duration.ZERO);
    }

    public static RemainingTime of(Duration remaining) {
        if (remaining == null || remaining.isNegative()) {
            throw new IllegalArgumentException("Remaining time must be non-negative");
        }
        return new RemainingTime(true, remaining);
    }

    public boolean isKnown() {
        return known;
    }

    public Optional<Duration> remaining() {
        return known ? Optional.of(remaining) : Optional.empty();
    }

    public boolean hasAtLeast(Duration minimum) {
        return known && remaining.compareTo(minimum) >= 0;
    }
}
