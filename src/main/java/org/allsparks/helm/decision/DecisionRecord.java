package org.allsparks.helm.decision;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.allsparks.helm.task.TaskEvaluation;

/**
 * Explainable decision or recommendation record. Shadow selection is not
 * implemented in this scaffold; the record still explains why.
 */
public final class DecisionRecord {
    private final long decisionCycle;
    private final String snapshotId;
    private final Optional<String> selectedTask;
    private final List<TaskEvaluation> evaluations;
    private final String explanation;
    private final boolean authoritative;
    private final boolean complete;

    public DecisionRecord(
            long decisionCycle,
            String snapshotId,
            Optional<String> selectedTask,
            List<TaskEvaluation> evaluations,
            String explanation,
            boolean authoritative,
            boolean complete) {
        this.decisionCycle = decisionCycle;
        this.snapshotId = Objects.requireNonNull(snapshotId, "snapshotId");
        this.selectedTask = selectedTask;
        this.evaluations = Collections.unmodifiableList(new ArrayList<>(evaluations));
        this.explanation = Objects.requireNonNull(explanation, "explanation");
        this.authoritative = authoritative;
        this.complete = complete;
    }

    public static DecisionRecord disabled(long cycle, String snapshotId, String explanation) {
        return new DecisionRecord(cycle, snapshotId, Optional.empty(), List.of(), explanation, false, true);
    }

    public long decisionCycle() {
        return decisionCycle;
    }

    public String snapshotId() {
        return snapshotId;
    }

    public Optional<String> selectedTask() {
        return selectedTask;
    }

    public List<TaskEvaluation> evaluations() {
        return evaluations;
    }

    public String explanation() {
        return explanation;
    }

    public boolean authoritative() {
        return authoritative;
    }

    public boolean complete() {
        return complete;
    }
}
