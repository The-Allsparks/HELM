package org.allsparks.helm.contracts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import java.util.Optional;
import org.allsparks.contracts.identity.CapabilityId;
import org.allsparks.contracts.observation.Validity;
import org.allsparks.contracts.status.Availability;
import org.allsparks.contracts.time.MonotonicClock;
import org.allsparks.helm.capability.Capability;
import org.allsparks.helm.capability.CapabilityAvailability;
import org.allsparks.helm.clock.ManualClock;
import org.allsparks.helm.clock.SystemNanoClock;
import org.allsparks.helm.confidence.Confidence;
import org.junit.jupiter.api.Test;

class HelmMappingsTest {
    @Test
    void namedCapabilityMapsOntoCapabilityId() {
        Capability custom = Capability.named("mechanism.lift");
        assertEquals(CapabilityId.of("mechanism.lift"), HelmMappings.toCapabilityId(custom));
        assertEquals(CapabilityId.of("DRIVE_TRANSLATION"), Capability.DRIVE_TRANSLATION.toCapabilityId());
    }

    @Test
    void availabilityMapsOntoSharedAvailability() {
        assertEquals(Optional.of(Availability.AVAILABLE),
                HelmMappings.toAvailability(CapabilityAvailability.AVAILABLE));
        assertEquals(Optional.of(Availability.DEGRADED),
                HelmMappings.toAvailability(CapabilityAvailability.DEGRADED));
        assertEquals(Optional.of(Availability.UNAVAILABLE),
                HelmMappings.toAvailability(CapabilityAvailability.UNAVAILABLE));
        assertEquals(Optional.of(Availability.UNKNOWN),
                HelmMappings.toAvailability(CapabilityAvailability.UNKNOWN));
    }

    @Test
    void staleIsFreshnessNotAFifthAvailability() {
        assertEquals(Optional.empty(), HelmMappings.toAvailability(CapabilityAvailability.STALE));
        assertEquals(Optional.of(Validity.STALE), HelmMappings.toValidity(CapabilityAvailability.STALE));
        assertFalse(CapabilityAvailability.STALE.mayBeUsed(true));
        assertFalse(CapabilityAvailability.UNKNOWN.mayBeUsed(true));
    }

    @Test
    void nonStaleAvailabilityIsNotAValidityLabel() {
        assertEquals(Optional.empty(), HelmMappings.toValidity(CapabilityAvailability.AVAILABLE));
        assertEquals(Optional.empty(), HelmMappings.toValidity(CapabilityAvailability.DEGRADED));
        assertEquals(Optional.empty(), HelmMappings.toValidity(CapabilityAvailability.UNAVAILABLE));
        assertEquals(Optional.empty(), HelmMappings.toValidity(CapabilityAvailability.UNKNOWN));
    }

    @Test
    void everyAvailabilityIsMappedOrExplicitlyUnmapped() {
        EnumSet<CapabilityAvailability> freshnessOnly = EnumSet.of(CapabilityAvailability.STALE);
        for (CapabilityAvailability availability : CapabilityAvailability.values()) {
            Optional<Availability> mapped = HelmMappings.toAvailability(availability);
            Optional<Validity> freshness = HelmMappings.toValidity(availability);
            if (freshnessOnly.contains(availability)) {
                assertFalse(mapped.isPresent(), availability.name());
                assertEquals(Optional.of(Validity.STALE), freshness);
            } else {
                assertTrue(mapped.isPresent(), availability.name());
                assertFalse(freshness.isPresent(), availability.name());
            }
        }
    }

    @Test
    void unknownAndKnownConfidenceRoundTrip() {
        Confidence unknown = Confidence.unknown();
        org.allsparks.contracts.observation.Confidence contractsUnknown = HelmMappings.toContracts(unknown);
        assertFalse(contractsUnknown.isKnown());
        assertEquals(unknown, HelmMappings.fromContracts(contractsUnknown));

        Confidence known = Confidence.of(0.75d);
        org.allsparks.contracts.observation.Confidence contractsKnown = HelmMappings.toContracts(known);
        assertTrue(contractsKnown.isKnown());
        assertEquals(0.75d, contractsKnown.value().getAsDouble(), 0.0d);
        assertEquals(known, HelmMappings.fromContracts(contractsKnown));
    }

    @Test
    void helmClockNowNanosDelegatesToNanoTime() {
        ManualClock clock = new ManualClock(1_000_000L);
        MonotonicClock monotonic = clock;
        assertEquals(1_000_000L, clock.nanoTime());
        assertEquals(clock.nanoTime(), clock.nowNanos());
        assertEquals(clock.nanoTime(), monotonic.nowNanos());
        assertTrue(new SystemNanoClock() instanceof MonotonicClock);
    }

    @Test
    void nullInputsAreRejected() {
        assertThrows(NullPointerException.class, () -> HelmMappings.toCapabilityId(null));
        assertThrows(NullPointerException.class, () -> HelmMappings.toAvailability(null));
        assertThrows(NullPointerException.class, () -> HelmMappings.toValidity(null));
        assertThrows(NullPointerException.class, () -> HelmMappings.toContracts(null));
        assertThrows(NullPointerException.class, () -> HelmMappings.fromContracts(null));
    }
}
