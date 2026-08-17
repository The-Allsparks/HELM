package org.allsparks.helm.capability;

/**
 * Semantic capability health as reported by a lower layer. HELM does not
 * diagnose the underlying hardware fault.
 *
 * <p>{@link #UNKNOWN} and {@link #STALE} must not be treated as available.
 */
public enum CapabilityAvailability {
    AVAILABLE,
    DEGRADED,
    UNAVAILABLE,
    UNKNOWN,
    STALE;

    public boolean mayBeUsed(boolean allowDegraded) {
        if (this == AVAILABLE) {
            return true;
        }
        if (this == DEGRADED) {
            return allowDegraded;
        }
        return false;
    }

    public boolean isKnownPresent() {
        return this == AVAILABLE || this == DEGRADED;
    }
}
