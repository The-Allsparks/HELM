package org.allsparks.helm.task;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.allsparks.helm.capability.Capability;
import org.allsparks.helm.condition.Condition;
import org.allsparks.helm.confidence.ConfidenceRequirement;
import org.allsparks.helm.goal.Goal;
import org.allsparks.helm.resource.Resource;

/**
 * Named action that may advance a goal. Season point values stay outside core.
 */
public final class Task {
    private final String name;
    private final Goal goal;
    private final List<Capability> requiredCapabilities;
    private final boolean allowDegradedCapabilities;
    private final List<Resource> requiredResources;
    private final List<ConfidenceRequirement> confidenceRequirements;
    private final List<Condition> preconditions;
    private final List<Condition> completionConditions;
    private final List<Condition> failureConditions;
    private final Optional<TimeoutPolicy> timeout;
    private final RetryPolicy retryPolicy;
    private final Optional<String> fallbackTaskName;
    private final Optional<Duration> expectedDuration;
    private final Optional<Duration> minimumRemainingTime;
    private final boolean allowUnknownSafetyAsAvailable;

    private Task(Builder builder) {
        this.name = requireText(builder.name, "name");
        this.goal = builder.goal == null ? Goal.named(builder.name) : builder.goal;
        this.requiredCapabilities = Collections.unmodifiableList(List.copyOf(builder.requiredCapabilities));
        this.allowDegradedCapabilities = builder.allowDegradedCapabilities;
        this.requiredResources = Collections.unmodifiableList(List.copyOf(builder.requiredResources));
        this.confidenceRequirements = Collections.unmodifiableList(List.copyOf(builder.confidenceRequirements));
        this.preconditions = Collections.unmodifiableList(List.copyOf(builder.preconditions));
        this.completionConditions = Collections.unmodifiableList(List.copyOf(builder.completionConditions));
        this.failureConditions = Collections.unmodifiableList(List.copyOf(builder.failureConditions));
        this.timeout = Optional.ofNullable(builder.timeout);
        this.retryPolicy = builder.retryPolicy == null ? RetryPolicy.none() : builder.retryPolicy;
        this.fallbackTaskName = Optional.ofNullable(builder.fallbackTaskName).filter(s -> !s.isBlank());
        this.expectedDuration = Optional.ofNullable(builder.expectedDuration);
        this.minimumRemainingTime = Optional.ofNullable(builder.minimumRemainingTime);
        this.allowUnknownSafetyAsAvailable = false;
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public String name() {
        return name;
    }

    public Goal goal() {
        return goal;
    }

    public List<Capability> requiredCapabilities() {
        return requiredCapabilities;
    }

    public boolean allowDegradedCapabilities() {
        return allowDegradedCapabilities;
    }

    public List<Resource> requiredResources() {
        return requiredResources;
    }

    public List<ConfidenceRequirement> confidenceRequirements() {
        return confidenceRequirements;
    }

    public List<Condition> preconditions() {
        return preconditions;
    }

    public List<Condition> completionConditions() {
        return completionConditions;
    }

    public List<Condition> failureConditions() {
        return failureConditions;
    }

    public Optional<TimeoutPolicy> timeout() {
        return timeout;
    }

    public RetryPolicy retryPolicy() {
        return retryPolicy;
    }

    public Optional<String> fallbackTaskName() {
        return fallbackTaskName;
    }

    public Optional<Duration> expectedDuration() {
        return expectedDuration;
    }

    public Optional<Duration> minimumRemainingTime() {
        return minimumRemainingTime;
    }

    public boolean allowUnknownSafetyAsAvailable() {
        return allowUnknownSafetyAsAvailable;
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
        private Goal goal;
        private final List<Capability> requiredCapabilities = new ArrayList<>();
        private boolean allowDegradedCapabilities;
        private final List<Resource> requiredResources = new ArrayList<>();
        private final List<ConfidenceRequirement> confidenceRequirements = new ArrayList<>();
        private final List<Condition> preconditions = new ArrayList<>();
        private final List<Condition> completionConditions = new ArrayList<>();
        private final List<Condition> failureConditions = new ArrayList<>();
        private TimeoutPolicy timeout;
        private RetryPolicy retryPolicy = RetryPolicy.none();
        private String fallbackTaskName;
        private Duration expectedDuration;
        private Duration minimumRemainingTime;

        private Builder(String name) {
            this.name = name;
        }

        public Builder goal(Goal goal) {
            this.goal = goal;
            return this;
        }

        public Builder requires(Capability capability) {
            this.requiredCapabilities.add(Objects.requireNonNull(capability, "capability"));
            return this;
        }

        public Builder allowDegradedCapabilities(boolean allowDegradedCapabilities) {
            this.allowDegradedCapabilities = allowDegradedCapabilities;
            return this;
        }

        public Builder requires(Resource resource) {
            this.requiredResources.add(Objects.requireNonNull(resource, "resource"));
            return this;
        }

        public Builder requires(ConfidenceRequirement requirement) {
            this.confidenceRequirements.add(Objects.requireNonNull(requirement, "requirement"));
            return this;
        }

        public Builder precondition(Condition condition) {
            this.preconditions.add(Objects.requireNonNull(condition, "condition"));
            return this;
        }

        public Builder completion(Condition condition) {
            this.completionConditions.add(Objects.requireNonNull(condition, "condition"));
            return this;
        }

        public Builder failure(Condition condition) {
            this.failureConditions.add(Objects.requireNonNull(condition, "condition"));
            return this;
        }

        public Builder timeout(Duration duration) {
            this.timeout = TimeoutPolicy.of(duration);
            return this;
        }

        public Builder timeout(TimeoutPolicy timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder retry(RetryPolicy retryPolicy) {
            this.retryPolicy = retryPolicy;
            return this;
        }

        public Builder fallback(String fallbackTaskName) {
            this.fallbackTaskName = fallbackTaskName;
            return this;
        }

        public Builder expectedDuration(Duration expectedDuration) {
            this.expectedDuration = expectedDuration;
            return this;
        }

        public Builder minimumRemainingTime(Duration minimumRemainingTime) {
            this.minimumRemainingTime = minimumRemainingTime;
            return this;
        }

        public Task build() {
            return new Task(this);
        }
    }
}
