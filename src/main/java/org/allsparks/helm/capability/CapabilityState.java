package org.allsparks.helm.capability;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Capability report from a named provider. HELM selects compatible tasks; it
 * does not interpret raw electrical or communication faults.
 */
public final class CapabilityState {
    private final Capability capability;
    private final CapabilityAvailability availability;
    private final String provider;
    private final long timestampNanos;
    private final String reason;
    private final List<String> restrictions;
    private final Optional<String> expectedRecovery;
    private final boolean usableByCurrentTask;

    private CapabilityState(Builder builder) {
        this.capability = Objects.requireNonNull(builder.capability, "capability");
        this.availability = Objects.requireNonNull(builder.availability, "availability");
        this.provider = requireText(builder.provider, "provider");
        this.timestampNanos = builder.timestampNanos;
        this.reason = builder.reason == null ? "" : builder.reason;
        this.restrictions = Collections.unmodifiableList(List.copyOf(builder.restrictions));
        this.expectedRecovery = Optional.ofNullable(builder.expectedRecovery)
                .filter(text -> !text.isBlank());
        this.usableByCurrentTask = builder.usableByCurrentTask;
    }

    public static Builder builder(Capability capability) {
        return new Builder(capability);
    }

    public static CapabilityState unknown(Capability capability, String provider, long timestampNanos) {
        return builder(capability)
                .availability(CapabilityAvailability.UNKNOWN)
                .provider(provider)
                .timestampNanos(timestampNanos)
                .reason("Capability state was not reported")
                .usableByCurrentTask(false)
                .build();
    }

    public Capability capability() {
        return capability;
    }

    public CapabilityAvailability availability() {
        return availability;
    }

    public String provider() {
        return provider;
    }

    public long timestampNanos() {
        return timestampNanos;
    }

    public String reason() {
        return reason;
    }

    public List<String> restrictions() {
        return restrictions;
    }

    public Optional<String> expectedRecovery() {
        return expectedRecovery;
    }

    public boolean usableByCurrentTask() {
        return usableByCurrentTask;
    }

    public boolean satisfies(boolean allowDegraded) {
        return availability.mayBeUsed(allowDegraded) && usableByCurrentTask;
    }

    private static String requireText(String value, String field) {
        Objects.requireNonNull(value, field);
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return trimmed;
    }

    public static final class Builder {
        private final Capability capability;
        private CapabilityAvailability availability = CapabilityAvailability.UNKNOWN;
        private String provider = "unspecified";
        private long timestampNanos;
        private String reason = "";
        private List<String> restrictions = List.of();
        private String expectedRecovery;
        private boolean usableByCurrentTask = true;

        private Builder(Capability capability) {
            this.capability = capability;
        }

        public Builder availability(CapabilityAvailability availability) {
            this.availability = availability;
            return this;
        }

        public Builder provider(String provider) {
            this.provider = provider;
            return this;
        }

        public Builder timestampNanos(long timestampNanos) {
            this.timestampNanos = timestampNanos;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public Builder restrictions(List<String> restrictions) {
            this.restrictions = restrictions == null ? List.of() : restrictions;
            return this;
        }

        public Builder expectedRecovery(String expectedRecovery) {
            this.expectedRecovery = expectedRecovery;
            return this;
        }

        public Builder usableByCurrentTask(boolean usableByCurrentTask) {
            this.usableByCurrentTask = usableByCurrentTask;
            return this;
        }

        public CapabilityState build() {
            return new CapabilityState(this);
        }
    }
}
