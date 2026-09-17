package org.allsparks.helm.contracts;

import java.util.Objects;
import java.util.Optional;
import org.allsparks.contracts.identity.CapabilityId;
import org.allsparks.contracts.observation.Validity;
import org.allsparks.contracts.status.Availability;
import org.allsparks.helm.capability.Capability;
import org.allsparks.helm.capability.CapabilityAvailability;
import org.allsparks.helm.confidence.Confidence;

/**
 * Edge mappings from HELM domain types onto allsparks-contracts envelopes.
 *
 * <p>Local {@link Capability}, {@link CapabilityAvailability}, and
 * {@link Confidence} stay in HELM. {@link CapabilityAvailability#STALE} is
 * freshness, not a fifth {@link Availability}; map it to {@link Validity#STALE}.
 * {@link CapabilityAvailability#mayBeUsed(boolean)} remains HELM policy.
 */
public final class HelmMappings {
    private HelmMappings() {}

    /**
     * Maps a HELM capability onto a shared identifier. Well-known constants
     * stay in HELM; custom names do not require a contracts change.
     *
     * @param capability HELM capability
     * @return shared identifier for the same name
     */
    public static CapabilityId toCapabilityId(Capability capability) {
        Objects.requireNonNull(capability, "capability");
        return capability.toCapabilityId();
    }

    /**
     * Maps HELM availability onto shared {@link Availability} when the meaning
     * matches.
     *
     * <p>{@link CapabilityAvailability#STALE} does not become a fifth
     * availability value. Use {@link #toValidity(CapabilityAvailability)} for
     * freshness. HELM still refuses STALE via local {@code mayBeUsed}.
     *
     * @param availability HELM capability availability
     * @return mapped availability, or empty for {@code STALE}
     */
    public static Optional<Availability> toAvailability(CapabilityAvailability availability) {
        Objects.requireNonNull(availability, "availability");
        switch (availability) {
            case AVAILABLE:
                return Optional.of(Availability.AVAILABLE);
            case DEGRADED:
                return Optional.of(Availability.DEGRADED);
            case UNAVAILABLE:
                return Optional.of(Availability.UNAVAILABLE);
            case UNKNOWN:
                return Optional.of(Availability.UNKNOWN);
            case STALE:
                return Optional.empty();
            default:
                throw new IllegalArgumentException("Unknown availability: " + availability);
        }
    }

    /**
     * Maps HELM availability onto shared {@link Validity} when it is a
     * freshness label.
     *
     * @param availability HELM capability availability
     * @return {@link Validity#STALE} for {@code STALE}, otherwise empty
     */
    public static Optional<Validity> toValidity(CapabilityAvailability availability) {
        Objects.requireNonNull(availability, "availability");
        if (availability == CapabilityAvailability.STALE) {
            return Optional.of(Validity.STALE);
        }
        return Optional.empty();
    }

    /**
     * Maps HELM confidence onto the shared envelope. Unknown stays unknown;
     * known values stay in {@code [0, 1]}.
     *
     * @param confidence HELM confidence
     * @return shared confidence with the same known/unknown state
     */
    public static org.allsparks.contracts.observation.Confidence toContracts(
            Confidence confidence) {
        Objects.requireNonNull(confidence, "confidence");
        if (!confidence.isKnown()) {
            return org.allsparks.contracts.observation.Confidence.unknown();
        }
        return org.allsparks.contracts.observation.Confidence.of(confidence.value().getAsDouble());
    }

    /**
     * Maps shared confidence onto HELM's local type. The HELM type is not
     * deleted.
     *
     * @param confidence shared confidence
     * @return HELM confidence with the same known/unknown state
     */
    public static Confidence fromContracts(org.allsparks.contracts.observation.Confidence confidence) {
        Objects.requireNonNull(confidence, "confidence");
        if (!confidence.isKnown()) {
            return Confidence.unknown();
        }
        return Confidence.of(confidence.value().getAsDouble());
    }
}
