package org.allsparks.helm.trace;

/**
 * TRACE adapter. Active HELM authority requires a validated recorder; this
 * scaffold ships a no-op sink for tests.
 */
public interface TraceSink {
    void record(TraceEvent event);

    boolean isNoOp();

    boolean isValidated();
}
