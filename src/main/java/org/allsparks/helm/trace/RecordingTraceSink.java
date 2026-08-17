package org.allsparks.helm.trace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * In-memory TRACE sink for tests and replay isolation checks.
 */
public final class RecordingTraceSink implements TraceSink {
    private final List<TraceEvent> events = new ArrayList<>();
    private final boolean validated;

    public RecordingTraceSink() {
        this(false);
    }

    public RecordingTraceSink(boolean validated) {
        this.validated = validated;
    }

    @Override
    public void record(TraceEvent event) {
        events.add(event);
    }

    @Override
    public boolean isNoOp() {
        return false;
    }

    @Override
    public boolean isValidated() {
        return validated;
    }

    public List<TraceEvent> events() {
        return Collections.unmodifiableList(events);
    }
}
