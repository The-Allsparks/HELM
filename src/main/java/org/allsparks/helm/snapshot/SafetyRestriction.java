package org.allsparks.helm.snapshot;

import java.util.Objects;
import java.util.Optional;

/**
 * Lower-layer safety restriction. HELM may choose among permitted actions; it
 * may not weaken or bypass the restriction.
 */
public final class SafetyRestriction {
    private final String id;
    private final String source;
    private final String description;
    private final boolean blocksAllMotion;
    private final long timestampNanos;

    public SafetyRestriction(String id, String source, String description, boolean blocksAllMotion, long timestampNanos) {
        this.id = requireText(id, "id");
        this.source = requireText(source, "source");
        this.description = description == null ? "" : description;
        this.blocksAllMotion = blocksAllMotion;
        this.timestampNanos = timestampNanos;
    }

    public String id() {
        return id;
    }

    public String source() {
        return source;
    }

    public String description() {
        return description;
    }

    public boolean blocksAllMotion() {
        return blocksAllMotion;
    }

    public long timestampNanos() {
        return timestampNanos;
    }

    public Optional<String> blockedExplanation() {
        return Optional.of(source + ": " + description);
    }

    private static String requireText(String text, String field) {
        Objects.requireNonNull(text, field);
        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return trimmed;
    }
}
