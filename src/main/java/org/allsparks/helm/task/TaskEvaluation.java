package org.allsparks.helm.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.allsparks.helm.outcome.FailureReason;

/**
 * Eligibility result for a single task against one world snapshot.
 */
public final class TaskEvaluation {
    private final String taskName;
    private final String snapshotId;
    private final boolean eligible;
    private final boolean complete;
    private final String explanation;
    private final List<FailureReason> rejectionReasons;
    private final List<String> details;
    private final long evaluationNanos;

    private TaskEvaluation(Builder builder) {
        this.taskName = Objects.requireNonNull(builder.taskName, "taskName");
        this.snapshotId = Objects.requireNonNull(builder.snapshotId, "snapshotId");
        this.eligible = builder.eligible;
        this.complete = builder.complete;
        this.explanation = builder.explanation == null ? "" : builder.explanation;
        this.rejectionReasons = Collections.unmodifiableList(List.copyOf(builder.rejectionReasons));
        this.details = Collections.unmodifiableList(List.copyOf(builder.details));
        this.evaluationNanos = builder.evaluationNanos;
    }

    public static Builder builder(String taskName, String snapshotId) {
        return new Builder(taskName, snapshotId);
    }

    public String taskName() {
        return taskName;
    }

    public String snapshotId() {
        return snapshotId;
    }

    public boolean isEligible() {
        return eligible && complete;
    }

    public boolean isComplete() {
        return complete;
    }

    public String explanation() {
        return explanation;
    }

    public List<FailureReason> rejectionReasons() {
        return rejectionReasons;
    }

    public List<String> details() {
        return details;
    }

    public long evaluationNanos() {
        return evaluationNanos;
    }

    public static final class Builder {
        private final String taskName;
        private final String snapshotId;
        private boolean eligible = true;
        private boolean complete = true;
        private String explanation = "";
        private final List<FailureReason> rejectionReasons = new ArrayList<>();
        private final List<String> details = new ArrayList<>();
        private long evaluationNanos;

        private Builder(String taskName, String snapshotId) {
            this.taskName = taskName;
            this.snapshotId = snapshotId;
        }

        public Builder eligible(boolean eligible) {
            this.eligible = eligible;
            return this;
        }

        public Builder complete(boolean complete) {
            this.complete = complete;
            return this;
        }

        public Builder explanation(String explanation) {
            this.explanation = explanation;
            return this;
        }

        public Builder reject(FailureReason reason, String detail) {
            this.eligible = false;
            this.rejectionReasons.add(reason);
            this.details.add(detail);
            return this;
        }

        public Builder note(String detail) {
            this.details.add(detail);
            return this;
        }

        public Builder evaluationNanos(long evaluationNanos) {
            this.evaluationNanos = evaluationNanos;
            return this;
        }

        public TaskEvaluation build() {
            if (explanation.isBlank()) {
                if (!complete) {
                    explanation = "Evaluation did not finish within the decision-time budget";
                } else if (eligible) {
                    explanation = "Task '" + taskName + "' is eligible";
                } else if (rejectionReasons.isEmpty()) {
                    explanation = "Task '" + taskName + "' is not eligible";
                } else {
                    explanation = "Task '" + taskName + "' is not eligible: "
                            + rejectionReasons.get(0).explanation();
                }
            }
            return new TaskEvaluation(this);
        }
    }
}
