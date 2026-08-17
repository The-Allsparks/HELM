package org.allsparks.helm.condition;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;

import org.allsparks.helm.confidence.Confidence;

/**
 * Result of evaluating a named condition against a world snapshot.
 */
public final class ConditionResult {
    private final String name;
    private final ConditionValue value;
    private final String source;
    private final long timestampNanos;
    private final long ageNanos;
    private final Confidence confidence;
    private final String explanation;
    private final List<String> evidenceIds;
    private final OptionalDouble requiredThreshold;
    private final OptionalDouble actualValue;

    private ConditionResult(Builder builder) {
        this.name = requireText(builder.name, "name");
        this.value = Objects.requireNonNull(builder.value, "value");
        this.source = requireText(builder.source, "source");
        this.timestampNanos = builder.timestampNanos;
        this.ageNanos = builder.ageNanos;
        this.confidence = builder.confidence == null ? Confidence.unknown() : builder.confidence;
        this.explanation = builder.explanation == null ? "" : builder.explanation;
        this.evidenceIds = Collections.unmodifiableList(List.copyOf(builder.evidenceIds));
        this.requiredThreshold = builder.requiredThreshold;
        this.actualValue = builder.actualValue;
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static ConditionResult unknown(String name, String source, long timestampNanos, String explanation) {
        return builder(name)
                .value(ConditionValue.UNKNOWN)
                .source(source)
                .timestampNanos(timestampNanos)
                .explanation(explanation)
                .build();
    }

    public static ConditionResult stale(String name, String source, long timestampNanos, long ageNanos, String explanation) {
        return builder(name)
                .value(ConditionValue.STALE)
                .source(source)
                .timestampNanos(timestampNanos)
                .ageNanos(ageNanos)
                .explanation(explanation)
                .build();
    }

    public String name() {
        return name;
    }

    public ConditionValue value() {
        return value;
    }

    public String source() {
        return source;
    }

    public long timestampNanos() {
        return timestampNanos;
    }

    public long ageNanos() {
        return ageNanos;
    }

    public Confidence confidence() {
        return confidence;
    }

    public String explanation() {
        return explanation;
    }

    public List<String> evidenceIds() {
        return evidenceIds;
    }

    public OptionalDouble requiredThreshold() {
        return requiredThreshold;
    }

    public OptionalDouble actualValue() {
        return actualValue;
    }

    public boolean isKnownTrue() {
        return value.isKnownTrue();
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
        private final String name;
        private ConditionValue value = ConditionValue.UNKNOWN;
        private String source = "unspecified";
        private long timestampNanos;
        private long ageNanos;
        private Confidence confidence = Confidence.unknown();
        private String explanation = "";
        private List<String> evidenceIds = List.of();
        private OptionalDouble requiredThreshold = OptionalDouble.empty();
        private OptionalDouble actualValue = OptionalDouble.empty();

        private Builder(String name) {
            this.name = name;
        }

        public Builder value(ConditionValue value) {
            this.value = value;
            return this;
        }

        public Builder source(String source) {
            this.source = source;
            return this;
        }

        public Builder timestampNanos(long timestampNanos) {
            this.timestampNanos = timestampNanos;
            return this;
        }

        public Builder ageNanos(long ageNanos) {
            this.ageNanos = ageNanos;
            return this;
        }

        public Builder confidence(Confidence confidence) {
            this.confidence = confidence;
            return this;
        }

        public Builder explanation(String explanation) {
            this.explanation = explanation;
            return this;
        }

        public Builder evidenceIds(List<String> evidenceIds) {
            this.evidenceIds = evidenceIds == null ? List.of() : evidenceIds;
            return this;
        }

        public Builder requiredThreshold(double requiredThreshold) {
            this.requiredThreshold = OptionalDouble.of(requiredThreshold);
            return this;
        }

        public Builder actualValue(double actualValue) {
            this.actualValue = OptionalDouble.of(actualValue);
            return this;
        }

        public ConditionResult build() {
            return new ConditionResult(this);
        }
    }
}
