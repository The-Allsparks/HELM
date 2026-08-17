package org.allsparks.helm.clock;

/**
 * Deterministic clock for tests and replay. Starts at zero unless constructed
 * with an explicit origin.
 */
public final class ManualClock implements HelmClock {
    private long nanos;

    public ManualClock() {
        this(0L);
    }

    public ManualClock(long initialNanos) {
        this.nanos = initialNanos;
    }

    @Override
    public long nanoTime() {
        return nanos;
    }

    public void setNanos(long nanos) {
        this.nanos = nanos;
    }

    public void advanceNanos(long delta) {
        if (delta < 0L) {
            throw new IllegalArgumentException("Clock cannot move backwards: " + delta);
        }
        this.nanos += delta;
    }

    public void advanceMillis(long millis) {
        advanceNanos(millis * 1_000_000L);
    }
}
