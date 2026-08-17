package org.allsparks.helm.clock;

/**
 * Production clock backed by {@link System#nanoTime()}.
 *
 * <p>Replay and unit tests should use {@link ManualClock} so decisions do not
 * depend on wall-clock jitter.
 */
public final class SystemNanoClock implements HelmClock {
    @Override
    public long nanoTime() {
        return System.nanoTime();
    }
}
