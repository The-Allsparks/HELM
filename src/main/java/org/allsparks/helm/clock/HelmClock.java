package org.allsparks.helm.clock;

import org.allsparks.contracts.time.MonotonicClock;

/**
 * Time source abstraction so tests and replay can advance time without hardware
 * or unrecorded wall-clock reads.
 *
 * <p>Units are nanoseconds since an arbitrary origin. Monotonic clocks are
 * preferred for durations; decision records must store the clock value used.
 *
 * <p>Extends {@link MonotonicClock}: {@link #nowNanos()} returns the same
 * reading as {@link #nanoTime()}. Keep {@link ManualClock} and
 * {@link SystemNanoClock}; they are not deleted by the contracts pilot.
 */
public interface HelmClock extends MonotonicClock {
    long nanoTime();

    @Override
    default long nowNanos() {
        return nanoTime();
    }
}
