package org.allsparks.helm.confidence;

import java.util.Objects;
import java.util.OptionalDouble;

/**
 * A confidence sample in {@code [0, 1]} or explicitly unknown. Unknown is not
 * zero and is not missing-as-absent.
 */
public final class Confidence {
    private final boolean known;
    private final double value;

    private Confidence(boolean known, double value) {
        this.known = known;
        this.value = value;
    }

    public static Confidence unknown() {
        return new Confidence(false, Double.NaN);
    }

    public static Confidence of(double value) {
        if (Double.isNaN(value) || value < 0.0d || value > 1.0d) {
            throw new IllegalArgumentException("Confidence must be in [0, 1], not " + value);
        }
        return new Confidence(true, value);
    }

    public boolean isKnown() {
        return known;
    }

    public OptionalDouble value() {
        return known ? OptionalDouble.of(value) : OptionalDouble.empty();
    }

    public boolean meets(double minimum) {
        return known && value >= minimum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Confidence)) {
            return false;
        }
        Confidence that = (Confidence) o;
        if (known != that.known) {
            return false;
        }
        return !known || Double.compare(value, that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(known, known ? value : 0.0d);
    }

    @Override
    public String toString() {
        return known ? String.format("%.3f", value) : "unknown";
    }
}
