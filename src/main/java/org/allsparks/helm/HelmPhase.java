package org.allsparks.helm;

/**
 * Learning and implementation phases. Higher phases build on lower phases and
 * remain disabled until students can explain the current phase.
 */
public enum HelmPhase {
    /** Vocabulary, immutable snapshots, eligibility language. No hardware. */
    PHASE_0_VOCABULARY,
    /** Passive observation of stated intent and outcomes. */
    PHASE_1_OBSERVE,
    /** Offline/static plan validation. Does not substitute another plan. */
    PHASE_2_VALIDATE,
    /** Static intent-tree execution. Requires a later approval gate. */
    PHASE_3_STATIC_EXECUTION,
    /** Shadow selection while existing auto remains authoritative. */
    PHASE_4_SHADOW,
    /** Bounded substitution among an explicit allowlist. Requires approval. */
    PHASE_5_BOUNDED,
    /** Acquisition-aware autonomous cycles. Not implemented. */
    PHASE_6_CYCLES,
    /** Capability-aware degraded operation. Not implemented. */
    PHASE_7_DEGRADED,
    /** Replay, regression, and fault injection. Partial in this scaffold. */
    PHASE_8_REPLAY,
    /** Predictive planning after real data exists. Not implemented. */
    PHASE_9_PREDICT
}
