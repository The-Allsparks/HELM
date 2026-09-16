package org.allsparks.helm.snapshot;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.allsparks.helm.confidence.Confidence;
import org.junit.jupiter.api.Test;

class PoseEstimateFreshnessTest {
    @Test
    void poseIsFreshInsideWindow() {
        PoseEstimate pose = PoseEstimate.builder()
                .xInches(12)
                .yInches(-4)
                .headingRadians(0.1)
                .timestampNanos(1_000_000L)
                .positionConfidence(Confidence.unknown())
                .provider("pedro")
                .build();
        assertTrue(pose.isFresh(1_050_000L, 100_000L));
        assertFalse(pose.isFresh(1_200_000L, 100_000L));
        assertFalse(pose.isFresh(900_000L, 100_000L));
    }

    @Test
    void missingSnapshotPoseIsNotFresh() {
        WorldSnapshot snapshot = WorldSnapshot.builder().timestampNanos(1_000_000L).build();
        assertFalse(snapshot.poseIsFresh(1_000_000L, 100_000_000L));
    }
}
