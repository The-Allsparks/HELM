package org.allsparks.helm.snapshot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.allsparks.helm.confidence.Confidence;
import org.junit.jupiter.api.Test;

class WorldSnapshotImmutabilityTest {
    @Test
    void snapshotCopiesCollectionsAndDoesNotExposeMutability() {
        List<ObservedTarget> targets = new ArrayList<>();
        targets.add(ObservedTarget.builder("a")
                .classification("sample")
                .classificationConfidence(Confidence.of(0.8d))
                .timestampNanos(0L)
                .build());
        WorldSnapshot snapshot = WorldSnapshot.builder()
                .snapshotId("immutable")
                .timestampNanos(10L)
                .targets(targets)
                .build();
        targets.clear();
        assertEquals(1, snapshot.targets().size());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.targets().add(
                ObservedTarget.builder("b").timestampNanos(0L).build()));
        WorldSnapshot copy = snapshot.toBuilder().snapshotId("copy").build();
        assertNotSame(snapshot, copy);
        assertEquals("copy", copy.snapshotId());
        assertTrue(snapshot.isFresh(10L, 1_000_000L));
    }
}
