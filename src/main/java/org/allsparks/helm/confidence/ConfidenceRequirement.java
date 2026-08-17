package org.allsparks.helm.confidence;

import java.util.Objects;

/**
 * A task's required minimum confidence on one dimension. Unknown or stale
 * samples fail the requirement.
 */
public final class ConfidenceRequirement {
    private final ConfidenceDimension dimension;
    private final double minimum;

    private ConfidenceRequirement(ConfidenceDimension dimension, double minimum) {
        this.dimension = Objects.requireNonNull(dimension, "dimension");
        if (Double.isNaN(minimum) || minimum < 0.0d || minimum > 1.0d) {
            throw new IllegalArgumentException("Minimum confidence must be in [0, 1]");
        }
        this.minimum = minimum;
    }

    public static ConfidenceRequirement of(ConfidenceDimension dimension, double minimum) {
        return new ConfidenceRequirement(dimension, minimum);
    }

    public ConfidenceDimension dimension() {
        return dimension;
    }

    public double minimum() {
        return minimum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConfidenceRequirement)) {
            return false;
        }
        ConfidenceRequirement that = (ConfidenceRequirement) o;
        return Double.compare(minimum, that.minimum) == 0 && dimension.equals(that.dimension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dimension, minimum);
    }

    @Override
    public String toString() {
        return dimension + ">=" + minimum;
    }
}
