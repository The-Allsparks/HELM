package org.allsparks.helm;

import java.time.Duration;
import java.util.Objects;

import org.allsparks.helm.clock.HelmClock;
import org.allsparks.helm.clock.ManualClock;
import org.allsparks.helm.clock.SystemNanoClock;
import org.allsparks.helm.trace.NoOpTraceSink;
import org.allsparks.helm.trace.TraceSink;

/**
 * Immutable HELM configuration. Defaults refuse execution authority.
 */
public final class HelmConfig {
    public static final String SCHEMA_VERSION = "0.1.0";
    public static final String HELM_VERSION = "0.1.0-SNAPSHOT";

    private final HelmMode mode;
    private final HelmFeatureFlags flags;
    private final HelmClock clock;
    private final TraceSink traceSink;
    private final Duration snapshotMaxAge;
    private final Duration decisionTimeBudget;
    private final int maxTreeDepth;
    private final int maxTreeNodes;
    private final int maxCandidates;
    private final boolean operatorDisable;
    private final boolean validatedTraceRecording;

    private HelmConfig(Builder builder) {
        this.mode = Objects.requireNonNull(builder.mode, "mode");
        this.flags = Objects.requireNonNull(builder.flags, "flags");
        this.clock = Objects.requireNonNull(builder.clock, "clock");
        this.traceSink = Objects.requireNonNull(builder.traceSink, "traceSink");
        this.snapshotMaxAge = Objects.requireNonNull(builder.snapshotMaxAge, "snapshotMaxAge");
        this.decisionTimeBudget = Objects.requireNonNull(builder.decisionTimeBudget, "decisionTimeBudget");
        this.maxTreeDepth = builder.maxTreeDepth;
        this.maxTreeNodes = builder.maxTreeNodes;
        this.maxCandidates = builder.maxCandidates;
        this.operatorDisable = builder.operatorDisable;
        this.validatedTraceRecording = builder.validatedTraceRecording;
        if (maxTreeDepth < 1 || maxTreeNodes < 1 || maxCandidates < 1) {
            throw new IllegalArgumentException("Tree and candidate limits must be positive");
        }
    }

    public static HelmConfig defaults() {
        return builder().build();
    }

    public static HelmConfig forTests(ManualClock clock) {
        return builder()
                .mode(HelmMode.VALIDATE)
                .flags(HelmFeatureFlags.validate())
                .clock(clock)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public HelmMode mode() {
        return mode;
    }

    public HelmFeatureFlags flags() {
        return flags;
    }

    public HelmClock clock() {
        return clock;
    }

    public TraceSink traceSink() {
        return traceSink;
    }

    public Duration snapshotMaxAge() {
        return snapshotMaxAge;
    }

    public Duration decisionTimeBudget() {
        return decisionTimeBudget;
    }

    public int maxTreeDepth() {
        return maxTreeDepth;
    }

    public int maxTreeNodes() {
        return maxTreeNodes;
    }

    public int maxCandidates() {
        return maxCandidates;
    }

    public boolean operatorDisable() {
        return operatorDisable;
    }

    public boolean validatedTraceRecording() {
        return validatedTraceRecording;
    }

    public static final class Builder {
        private HelmMode mode = HelmMode.OFF;
        private HelmFeatureFlags flags = HelmFeatureFlags.defaults();
        private HelmClock clock = new SystemNanoClock();
        private TraceSink traceSink = new NoOpTraceSink();
        private Duration snapshotMaxAge = Duration.ofMillis(100);
        private Duration decisionTimeBudget = Duration.ofMillis(5);
        private int maxTreeDepth = 16;
        private int maxTreeNodes = 64;
        private int maxCandidates = 8;
        private boolean operatorDisable = false;
        private boolean validatedTraceRecording = false;

        public Builder mode(HelmMode mode) {
            this.mode = mode;
            return this;
        }

        public Builder flags(HelmFeatureFlags flags) {
            this.flags = flags;
            return this;
        }

        public Builder clock(HelmClock clock) {
            this.clock = clock;
            return this;
        }

        public Builder traceSink(TraceSink traceSink) {
            this.traceSink = traceSink;
            return this;
        }

        public Builder snapshotMaxAge(Duration snapshotMaxAge) {
            this.snapshotMaxAge = snapshotMaxAge;
            return this;
        }

        public Builder decisionTimeBudget(Duration decisionTimeBudget) {
            this.decisionTimeBudget = decisionTimeBudget;
            return this;
        }

        public Builder maxTreeDepth(int maxTreeDepth) {
            this.maxTreeDepth = maxTreeDepth;
            return this;
        }

        public Builder maxTreeNodes(int maxTreeNodes) {
            this.maxTreeNodes = maxTreeNodes;
            return this;
        }

        public Builder maxCandidates(int maxCandidates) {
            this.maxCandidates = maxCandidates;
            return this;
        }

        public Builder operatorDisable(boolean operatorDisable) {
            this.operatorDisable = operatorDisable;
            return this;
        }

        public Builder validatedTraceRecording(boolean validatedTraceRecording) {
            this.validatedTraceRecording = validatedTraceRecording;
            return this;
        }

        public HelmConfig build() {
            return new HelmConfig(this);
        }
    }
}
