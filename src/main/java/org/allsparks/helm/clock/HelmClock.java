package org.allsparks.helm.clock;

/**
 * Time source abstraction so tests and replay can advance time without hardware
 * or unrecorded wall-clock reads.
 *
 * <p>Units are nanoseconds since an arbitrary origin. Monotonic clocks are
 * preferred for durations; decision records must store the clock value used.
 */
public interface HelmClock {
    long nanoTime();
}
