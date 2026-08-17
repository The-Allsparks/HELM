package org.allsparks.helm;

/**
 * Central feature flags. Execution-related flags default to {@code false}.
 *
 * <p>Installing an adapter must not enable a flag. Missing TRACE or unknown
 * safety state must not enable authority.
 */
public final class HelmFeatureFlags {
    private final boolean phase0Vocabulary;
    private final boolean phase1Observe;
    private final boolean phase2Validate;
    private final boolean phase3StaticExecution;
    private final boolean phase4Shadow;
    private final boolean phase5Bounded;
    private final boolean phase6Cycles;
    private final boolean phase7Degraded;
    private final boolean phase8Replay;
    private final boolean phase9Predict;
    private final boolean requireTraceForAuthority;

    private HelmFeatureFlags(Builder builder) {
        this.phase0Vocabulary = builder.phase0Vocabulary;
        this.phase1Observe = builder.phase1Observe;
        this.phase2Validate = builder.phase2Validate;
        this.phase3StaticExecution = builder.phase3StaticExecution;
        this.phase4Shadow = builder.phase4Shadow;
        this.phase5Bounded = builder.phase5Bounded;
        this.phase6Cycles = builder.phase6Cycles;
        this.phase7Degraded = builder.phase7Degraded;
        this.phase8Replay = builder.phase8Replay;
        this.phase9Predict = builder.phase9Predict;
        this.requireTraceForAuthority = builder.requireTraceForAuthority;
    }

    /** Safe defaults: Phase 0 vocabulary on; observation/validation off until enabled. */
    public static HelmFeatureFlags defaults() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static HelmFeatureFlags phase0() {
        return defaults();
    }

    public static HelmFeatureFlags observe() {
        return builder().phase1Observe(true).build();
    }

    public static HelmFeatureFlags validate() {
        return builder().phase1Observe(true).phase2Validate(true).build();
    }

    public boolean isPhase0Vocabulary() {
        return phase0Vocabulary;
    }

    public boolean isPhase1Observe() {
        return phase1Observe;
    }

    public boolean isPhase2Validate() {
        return phase2Validate;
    }

    public boolean isPhase3StaticExecution() {
        return phase3StaticExecution;
    }

    public boolean isPhase4Shadow() {
        return phase4Shadow;
    }

    public boolean isPhase5Bounded() {
        return phase5Bounded;
    }

    public boolean isPhase6Cycles() {
        return phase6Cycles;
    }

    public boolean isPhase7Degraded() {
        return phase7Degraded;
    }

    public boolean isPhase8Replay() {
        return phase8Replay;
    }

    public boolean isPhase9Predict() {
        return phase9Predict;
    }

    public boolean isRequireTraceForAuthority() {
        return requireTraceForAuthority;
    }

    /** True if any flag that could later issue lower-layer actions is enabled. */
    public boolean isAnyExecutionEnabled() {
        return phase3StaticExecution || phase5Bounded || phase6Cycles
                || phase7Degraded || phase9Predict;
    }

    public static final class Builder {
        private boolean phase0Vocabulary = true;
        private boolean phase1Observe = false;
        private boolean phase2Validate = false;
        private boolean phase3StaticExecution = false;
        private boolean phase4Shadow = false;
        private boolean phase5Bounded = false;
        private boolean phase6Cycles = false;
        private boolean phase7Degraded = false;
        private boolean phase8Replay = false;
        private boolean phase9Predict = false;
        private boolean requireTraceForAuthority = true;

        public Builder phase0Vocabulary(boolean value) {
            this.phase0Vocabulary = value;
            return this;
        }

        public Builder phase1Observe(boolean value) {
            this.phase1Observe = value;
            return this;
        }

        public Builder phase2Validate(boolean value) {
            this.phase2Validate = value;
            return this;
        }

        public Builder phase3StaticExecution(boolean value) {
            this.phase3StaticExecution = value;
            return this;
        }

        public Builder phase4Shadow(boolean value) {
            this.phase4Shadow = value;
            return this;
        }

        public Builder phase5Bounded(boolean value) {
            this.phase5Bounded = value;
            return this;
        }

        public Builder phase6Cycles(boolean value) {
            this.phase6Cycles = value;
            return this;
        }

        public Builder phase7Degraded(boolean value) {
            this.phase7Degraded = value;
            return this;
        }

        public Builder phase8Replay(boolean value) {
            this.phase8Replay = value;
            return this;
        }

        public Builder phase9Predict(boolean value) {
            this.phase9Predict = value;
            return this;
        }

        public Builder requireTraceForAuthority(boolean value) {
            this.requireTraceForAuthority = value;
            return this;
        }

        public HelmFeatureFlags build() {
            return new HelmFeatureFlags(this);
        }
    }
}
