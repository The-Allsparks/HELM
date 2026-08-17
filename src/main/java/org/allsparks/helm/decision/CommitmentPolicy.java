package org.allsparks.helm.decision;

import java.util.Objects;

/**
 * Deterministic anti-chatter helper for later selection phases. A slightly
 * higher score must not cause continuous task switching.
 */
public final class CommitmentPolicy {
    private final double materialImprovement;
    private final int minCyclesBeforeSwitch;

    public CommitmentPolicy(double materialImprovement, int minCyclesBeforeSwitch) {
        if (materialImprovement < 0.0d) {
            throw new IllegalArgumentException("materialImprovement must be non-negative");
        }
        if (minCyclesBeforeSwitch < 1) {
            throw new IllegalArgumentException("minCyclesBeforeSwitch must be at least 1");
        }
        this.materialImprovement = materialImprovement;
        this.minCyclesBeforeSwitch = minCyclesBeforeSwitch;
    }

    public static CommitmentPolicy defaults() {
        return new CommitmentPolicy(1.0d, 3);
    }

    public boolean shouldSwitch(
            String currentTask,
            String candidateTask,
            double currentScore,
            double candidateScore,
            int cyclesOnCurrent) {
        Objects.requireNonNull(currentTask, "currentTask");
        Objects.requireNonNull(candidateTask, "candidateTask");
        if (currentTask.equals(candidateTask)) {
            return false;
        }
        if (cyclesOnCurrent < minCyclesBeforeSwitch) {
            return false;
        }
        return candidateScore >= currentScore + materialImprovement;
    }

    public String tieBreak(String left, String right) {
        return left.compareTo(right) <= 0 ? left : right;
    }
}
