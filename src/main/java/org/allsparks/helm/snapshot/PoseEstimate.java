package org.allsparks.helm.snapshot;

import java.util.Objects;
import java.util.Optional;

import org.allsparks.helm.confidence.Confidence;

/**
 * Pose report from localization. HELM does not fuse sensors.
 */
public final class PoseEstimate {
    private final double xInches;
    private final double yInches;
    private final double headingRadians;
    private final long timestampNanos;
    private final Confidence positionConfidence;
    private final Confidence headingConfidence;
    private final String provider;

    private PoseEstimate(Builder builder) {
        this.xInches = builder.xInches;
        this.yInches = builder.yInches;
        this.headingRadians = builder.headingRadians;
        this.timestampNanos = builder.timestampNanos;
        this.positionConfidence = builder.positionConfidence == null
                ? Confidence.unknown() : builder.positionConfidence;
        this.headingConfidence = builder.headingConfidence == null
                ? Confidence.unknown() : builder.headingConfidence;
        this.provider = builder.provider == null ? "unspecified" : builder.provider;
    }

    public static Builder builder() {
        return new Builder();
    }

    public double xInches() {
        return xInches;
    }

    public double yInches() {
        return yInches;
    }

    public double headingRadians() {
        return headingRadians;
    }

    public long timestampNanos() {
        return timestampNanos;
    }

    public Confidence positionConfidence() {
        return positionConfidence;
    }

    public Confidence headingConfidence() {
        return headingConfidence;
    }

    public String provider() {
        return provider;
    }

    public Optional<Long> ageNanos(long nowNanos) {
        return Optional.of(Math.max(0L, nowNanos - timestampNanos));
    }

    public static final class Builder {
        private double xInches;
        private double yInches;
        private double headingRadians;
        private long timestampNanos;
        private Confidence positionConfidence = Confidence.unknown();
        private Confidence headingConfidence = Confidence.unknown();
        private String provider = "unspecified";

        private Builder() {
        }

        public Builder xInches(double xInches) {
            this.xInches = xInches;
            return this;
        }

        public Builder yInches(double yInches) {
            this.yInches = yInches;
            return this;
        }

        public Builder headingRadians(double headingRadians) {
            this.headingRadians = headingRadians;
            return this;
        }

        public Builder timestampNanos(long timestampNanos) {
            this.timestampNanos = timestampNanos;
            return this;
        }

        public Builder positionConfidence(Confidence positionConfidence) {
            this.positionConfidence = positionConfidence;
            return this;
        }

        public Builder headingConfidence(Confidence headingConfidence) {
            this.headingConfidence = headingConfidence;
            return this;
        }

        public Builder provider(String provider) {
            this.provider = Objects.requireNonNull(provider, "provider");
            return this;
        }

        public PoseEstimate build() {
            return new PoseEstimate(this);
        }
    }
}
