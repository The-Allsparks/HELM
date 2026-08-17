package org.allsparks.helm;

/**
 * Explicit operating mode. No mode activates merely because an adapter is on
 * the classpath.
 *
 * <p>Default is {@link #OFF}. Physical output is refused in every mode shipped
 * in this scaffold.
 */
public enum HelmMode {
    /** No planning, validation, execution, or recommendations. */
    OFF,
    /** Record the robot application's stated intent and outcomes. */
    OBSERVE,
    /** Check a manually selected plan without replacing it. */
    VALIDATE,
    /** Calculate what HELM would select while existing logic remains authoritative. */
    SHADOW,
    /** Execute a fixed student-authored intent tree. Not approved in this scaffold. */
    EXECUTE_STATIC,
    /** Select only among an explicitly approved task set. Not approved. */
    EXECUTE_BOUNDED,
    /** Capability-, confidence-, and time-aware task selection. Not approved. */
    ADAPTIVE,
    /** Re-evaluate recorded decisions without physical outputs. */
    REPLAY;

    public boolean recordsObservations() {
        return this == OBSERVE || this == VALIDATE || this == SHADOW
                || this == EXECUTE_STATIC || this == EXECUTE_BOUNDED
                || this == ADAPTIVE || this == REPLAY;
    }

    public boolean allowsEvaluation() {
        return this == OBSERVE || this == VALIDATE || this == SHADOW
                || this == EXECUTE_STATIC || this == EXECUTE_BOUNDED
                || this == ADAPTIVE || this == REPLAY;
    }

    public boolean allowsValidation() {
        return this == VALIDATE || this == SHADOW
                || this == EXECUTE_STATIC || this == EXECUTE_BOUNDED
                || this == ADAPTIVE || this == REPLAY;
    }

    public boolean allowsRecommendations() {
        return this == SHADOW || this == ADAPTIVE || this == EXECUTE_BOUNDED;
    }

    /**
     * True if this mode would request lower-layer actions when later phases are
     * approved. This scaffold still refuses physical output.
     */
    public boolean requestsExecutionAuthority() {
        return this == EXECUTE_STATIC || this == EXECUTE_BOUNDED || this == ADAPTIVE;
    }

    public boolean isReplay() {
        return this == REPLAY;
    }
}
