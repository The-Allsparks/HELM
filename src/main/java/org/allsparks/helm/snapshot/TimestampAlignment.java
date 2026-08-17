package org.allsparks.helm.snapshot;

import java.util.Objects;

/**
 * Detects whether snapshot sources share a compatible time base.
 */
public final class TimestampAlignment {
    private final boolean aligned;
    private final long maxSpreadNanos;
    private final String explanation;

    private TimestampAlignment(boolean aligned, long maxSpreadNanos, String explanation) {
        this.aligned = aligned;
        this.maxSpreadNanos = maxSpreadNanos;
        this.explanation = Objects.requireNonNull(explanation, "explanation");
    }

    public static TimestampAlignment aligned(long maxSpreadNanos) {
        return new TimestampAlignment(true, maxSpreadNanos,
                "Source timestamps are within the alignment window");
    }

    public static TimestampAlignment misaligned(long maxSpreadNanos, String explanation) {
        return new TimestampAlignment(false, maxSpreadNanos, explanation);
    }

    public static TimestampAlignment singleSource() {
        return new TimestampAlignment(true, 0L, "Snapshot contains a single timestamped source");
    }

    public boolean aligned() {
        return aligned;
    }

    public long maxSpreadNanos() {
        return maxSpreadNanos;
    }

    public String explanation() {
        return explanation;
    }
}
