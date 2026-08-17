package org.allsparks.helm.confidence;

import java.util.Objects;

/**
 * Named confidence dimension. Tasks declare exactly which dimensions they
 * require. There is no single global robot-confidence number.
 */
public final class ConfidenceDimension {
    public static final ConfidenceDimension POSITION = named("position");
    public static final ConfidenceDimension HEADING = named("heading");
    public static final ConfidenceDimension TARGET_CLASSIFICATION = named("target-classification");
    public static final ConfidenceDimension TARGET_POSITION = named("target-position");
    public static final ConfidenceDimension POSSESSION = named("possession");
    public static final ConfidenceDimension MECHANISM_STATE = named("mechanism-state");
    public static final ConfidenceDimension TIME_ESTIMATE = named("time-estimate");
    public static final ConfidenceDimension CAPABILITY_HEALTH = named("capability-health");

    private final String name;

    private ConfidenceDimension(String name) {
        this.name = name;
    }

    public static ConfidenceDimension named(String name) {
        Objects.requireNonNull(name, "name");
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Confidence dimension name must not be blank");
        }
        return new ConfidenceDimension(trimmed);
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConfidenceDimension)) {
            return false;
        }
        return name.equals(((ConfidenceDimension) o).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
