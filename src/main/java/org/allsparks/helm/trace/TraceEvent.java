package org.allsparks.helm.trace;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * One TRACE decision or observation event. Schema is stable enough for tests;
 * TRACE itself remains an external project.
 */
public final class TraceEvent {
    private final String type;
    private final long timestampNanos;
    private final String snapshotId;
    private final long decisionCycle;
    private final Map<String, String> fields;

    public TraceEvent(String type, long timestampNanos, String snapshotId, long decisionCycle, Map<String, String> fields) {
        this.type = Objects.requireNonNull(type, "type");
        this.timestampNanos = timestampNanos;
        this.snapshotId = snapshotId == null ? "" : snapshotId;
        this.decisionCycle = decisionCycle;
        this.fields = Collections.unmodifiableMap(new LinkedHashMap<>(fields));
    }

    public static TraceEvent of(String type, long timestampNanos, String snapshotId, long decisionCycle) {
        return new TraceEvent(type, timestampNanos, snapshotId, decisionCycle, Map.of());
    }

    public TraceEvent with(String key, String value) {
        Map<String, String> copy = new LinkedHashMap<>(fields);
        copy.put(key, value);
        return new TraceEvent(type, timestampNanos, snapshotId, decisionCycle, copy);
    }

    public String type() {
        return type;
    }

    public long timestampNanos() {
        return timestampNanos;
    }

    public String snapshotId() {
        return snapshotId;
    }

    public long decisionCycle() {
        return decisionCycle;
    }

    public Map<String, String> fields() {
        return fields;
    }
}
