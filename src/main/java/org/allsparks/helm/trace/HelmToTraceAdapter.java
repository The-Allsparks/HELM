package org.allsparks.helm.trace;

import java.util.Map;
import java.util.Objects;

/**
 * Maps HELM {@link TraceEvent} onto TRACE channel names. HELM does not import
 * TRACE. TeamCode supplies the {@link Emitter} that calls TRACE (FailOpen +
 * {@code integrationEnabled("HELM")}) when HELM is composed.
 *
 * <p>Does not replace {@link TraceEvent} with TRACE types. A no-op emitter is
 * never validated for execution authority.
 */
public final class HelmToTraceAdapter implements TraceSink {
    /**
     * TeamCode TRACE (or a test recorder). Must not block or throw into HELM.
     */
    public interface Emitter {
        void event(String name, String message);
    }

    private final Emitter emitter;
    private final boolean validated;

    public HelmToTraceAdapter(Emitter emitter) {
        this(emitter, false);
    }

    public HelmToTraceAdapter(Emitter emitter, boolean validated) {
        this.emitter = Objects.requireNonNull(emitter, "emitter");
        this.validated = validated;
    }

    @Override
    public void record(TraceEvent event) {
        if (event == null) {
            return;
        }
        emitter.event("HELM/" + sanitize(event.type()), payload(event));
    }

    @Override
    public boolean isNoOp() {
        return false;
    }

    @Override
    public boolean isValidated() {
        return validated;
    }

    static String payload(TraceEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append(event.timestampNanos());
        sb.append(' ').append(event.snapshotId());
        sb.append(' ').append(event.decisionCycle());
        for (Map.Entry<String, String> field : event.fields().entrySet()) {
            sb.append(' ').append(field.getKey()).append('=').append(field.getValue());
        }
        return sb.toString();
    }

    static String sanitize(String raw) {
        if (raw == null || raw.isEmpty()) {
            return "Event";
        }
        StringBuilder sb = new StringBuilder(raw.length());
        for (int i = 0; i < raw.length(); i++) {
            char ch = raw.charAt(i);
            if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || ch == '_') {
                sb.append(ch);
            } else {
                sb.append('_');
            }
        }
        if (sb.length() == 0) {
            return "Event";
        }
        char first = sb.charAt(0);
        if (first >= '0' && first <= '9') {
            sb.insert(0, 'H');
        }
        return sb.toString();
    }
}
