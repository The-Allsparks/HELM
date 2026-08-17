package org.allsparks.helm.snapshot;

import java.util.Objects;

import org.allsparks.helm.confidence.Confidence;

/**
 * Observed field target. Classification and position confidence are separate.
 */
public final class ObservedTarget {
    private final String id;
    private final String classification;
    private final Confidence classificationConfidence;
    private final double xInches;
    private final double yInches;
    private final Confidence positionConfidence;
    private final long timestampNanos;
    private final String provider;

    private ObservedTarget(Builder builder) {
        this.id = requireText(builder.id, "id");
        this.classification = builder.classification == null ? "unknown" : builder.classification;
        this.classificationConfidence = builder.classificationConfidence == null
                ? Confidence.unknown() : builder.classificationConfidence;
        this.xInches = builder.xInches;
        this.yInches = builder.yInches;
        this.positionConfidence = builder.positionConfidence == null
                ? Confidence.unknown() : builder.positionConfidence;
        this.timestampNanos = builder.timestampNanos;
        this.provider = builder.provider == null ? "unspecified" : builder.provider;
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public String id() {
        return id;
    }

    public String classification() {
        return classification;
    }

    public Confidence classificationConfidence() {
        return classificationConfidence;
    }

    public double xInches() {
        return xInches;
    }

    public double yInches() {
        return yInches;
    }

    public Confidence positionConfidence() {
        return positionConfidence;
    }

    public long timestampNanos() {
        return timestampNanos;
    }

    public String provider() {
        return provider;
    }

    private static String requireText(String text, String field) {
        Objects.requireNonNull(text, field);
        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return trimmed;
    }

    public static final class Builder {
        private final String id;
        private String classification = "unknown";
        private Confidence classificationConfidence = Confidence.unknown();
        private double xInches;
        private double yInches;
        private Confidence positionConfidence = Confidence.unknown();
        private long timestampNanos;
        private String provider = "unspecified";

        private Builder(String id) {
            this.id = id;
        }

        public Builder classification(String classification) {
            this.classification = classification;
            return this;
        }

        public Builder classificationConfidence(Confidence classificationConfidence) {
            this.classificationConfidence = classificationConfidence;
            return this;
        }

        public Builder xInches(double xInches) {
            this.xInches = xInches;
            return this;
        }

        public Builder yInches(double yInches) {
            this.yInches = yInches;
            return this;
        }

        public Builder positionConfidence(Confidence positionConfidence) {
            this.positionConfidence = positionConfidence;
            return this;
        }

        public Builder timestampNanos(long timestampNanos) {
            this.timestampNanos = timestampNanos;
            return this;
        }

        public Builder provider(String provider) {
            this.provider = provider;
            return this;
        }

        public ObservedTarget build() {
            return new ObservedTarget(this);
        }
    }
}
