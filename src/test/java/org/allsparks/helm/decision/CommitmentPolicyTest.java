package org.allsparks.helm.decision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CommitmentPolicyTest {
    private final CommitmentPolicy policy = new CommitmentPolicy(2.0d, 3);

    @Test
    void smallScoreIncreaseDoesNotSwitch() {
        assertFalse(policy.shouldSwitch("ScorePreload", "AcquireNearestPiece", 10.0d, 11.0d, 10));
    }

    @Test
    void materialImprovementStillHonorsCommitmentWindow() {
        assertFalse(policy.shouldSwitch("ScorePreload", "AcquireNearestPiece", 10.0d, 13.0d, 1));
        assertTrue(policy.shouldSwitch("ScorePreload", "AcquireNearestPiece", 10.0d, 13.0d, 3));
    }

    @Test
    void equalScoresUseNameOrderTieBreak() {
        assertEquals("AcquireNearestPiece", policy.tieBreak("ScorePreload", "AcquireNearestPiece"));
        assertEquals("AcquireNearestPiece", policy.tieBreak("AcquireNearestPiece", "ScorePreload"));
    }
}
