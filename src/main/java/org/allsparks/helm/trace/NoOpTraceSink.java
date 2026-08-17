package org.allsparks.helm.trace;

/**
 * Default TRACE adapter. Safe for unit tests. Must not be treated as validated
 * decision recording for execution authority.
 */
public final class NoOpTraceSink implements TraceSink {
    @Override
    public void record(TraceEvent event) {
        // Intentionally empty.
    }

    @Override
    public boolean isNoOp() {
        return true;
    }

    @Override
    public boolean isValidated() {
        return false;
    }
}
