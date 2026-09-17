package org.allsparks.helm.capability;

/**
 * Semantic capability health as reported by a lower layer. HELM does not
 * diagnose the underlying hardware fault.
 *
 * <p>{@link #UNKNOWN} and {@link #STALE} must not be treated as available.
 * Shared {@code Availability} has no {@code STALE} constant; map {@link #STALE}
 * through freshness ({@code Validity.STALE}) at the contracts edge.
 * {@link #mayBeUsed(boolean)} stays HELM policy on this local enum.
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
