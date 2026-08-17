package org.allsparks.helm.task;

import java.time.Duration;

import org.allsparks.helm.HelmConfig;
import org.allsparks.helm.capability.Capability;
import org.allsparks.helm.capability.CapabilityAvailability;
import org.allsparks.helm.capability.CapabilityState;
import org.allsparks.helm.condition.Condition;
import org.allsparks.helm.condition.ConditionResult;
import org.allsparks.helm.condition.ConditionValue;
import org.allsparks.helm.confidence.Confidence;
import org.allsparks.helm.confidence.ConfidenceRequirement;
import org.allsparks.helm.outcome.FailureReason;
import org.allsparks.helm.resource.Resource;
import org.allsparks.helm.snapshot.WorldSnapshot;

/**
 * Deterministic eligibility evaluation of one task against one snapshot.
 */
public final class TaskEvaluator {
    private final HelmConfig config;

    public TaskEvaluator(HelmConfig config) {
        this.config = config;
    }

    public TaskEvaluation evaluate(Task task, WorldSnapshot snapshot) {
        long start = config.clock().nanoTime();
        TaskEvaluation.Builder builder = TaskEvaluation.builder(task.name(), snapshot.snapshotId());
        if (config.operatorDisable() || snapshot.operatorDisable()) {
            builder.reject(FailureReason.OPERATOR_DISABLED, "Operator disable path is active");
        }
        if (config.mode() == org.allsparks.helm.HelmMode.OFF) {
            builder.reject(FailureReason.MODE_DISABLED, "HELM mode is OFF");
        } else if (!config.mode().allowsEvaluation()) {
            builder.reject(FailureReason.MODE_DISABLED, "Mode " + config.mode() + " does not allow evaluation");
        }
        if (!snapshot.timestampAlignment().aligned()) {
            builder.reject(FailureReason.STALE_INPUT, snapshot.timestampAlignment().explanation());
        }
        if (!snapshot.isFresh(config.clock().nanoTime(), config.snapshotMaxAge().toNanos())) {
            builder.reject(FailureReason.STALE_INPUT, "World snapshot is older than "
                    + config.snapshotMaxAge().toMillis() + " ms");
        }
        if (snapshot.hasBlockingSafetyRestriction()) {
            builder.reject(FailureReason.SAFETY_RESTRICTION,
                    snapshot.safetyRestrictions().get(0).blockedExplanation().orElse("Safety restriction"));
        }
        if (task.timeout().isEmpty()) {
            builder.reject(FailureReason.MISSING_TIMEOUT, "Task has no timeout");
        }
        evaluateCapabilities(task, snapshot, builder);
        evaluateConfidence(task, snapshot, builder);
        evaluateResources(task, snapshot, builder);
        evaluateConditions(task, snapshot, builder);
        evaluateTime(task, snapshot, builder);

        long elapsed = config.clock().nanoTime() - start;
        builder.evaluationNanos(elapsed);
        if (elapsed > config.decisionTimeBudget().toNanos()) {
            builder.complete(false)
                    .eligible(false)
                    .reject(FailureReason.TIME_BUDGET_EXCEEDED,
                            "Evaluation exceeded the " + config.decisionTimeBudget().toMillis()
                                    + " ms decision-time budget")
                    .explanation("Incomplete evaluation; current safe action must be preserved");
        }
        return builder.build();
    }

    private void evaluateCapabilities(Task task, WorldSnapshot snapshot, TaskEvaluation.Builder builder) {
        for (Capability capability : task.requiredCapabilities()) {
            CapabilityState state = snapshot.capabilityOrUnknown(capability);
            CapabilityAvailability availability = state.availability();
            if (availability == CapabilityAvailability.UNKNOWN) {
                builder.reject(FailureReason.CAPABILITY_UNKNOWN,
                        "Capability " + capability.name() + " is UNKNOWN from " + state.provider());
            } else if (availability == CapabilityAvailability.STALE) {
                builder.reject(FailureReason.STALE_INPUT,
                        "Capability " + capability.name() + " is STALE from " + state.provider());
            } else if (availability == CapabilityAvailability.UNAVAILABLE
                    || !state.satisfies(task.allowDegradedCapabilities())) {
                builder.reject(FailureReason.CAPABILITY_UNAVAILABLE,
                        "Capability " + capability.name() + " is " + availability
                                + (state.reason().isBlank() ? "" : " (" + state.reason() + ")"));
            } else {
                builder.note("Capability " + capability.name() + " is " + availability);
            }
        }
    }

    private void evaluateConfidence(Task task, WorldSnapshot snapshot, TaskEvaluation.Builder builder) {
        for (ConfidenceRequirement requirement : task.confidenceRequirements()) {
            Confidence sample = snapshot.confidence(requirement.dimension());
            if (!sample.isKnown()) {
                builder.reject(FailureReason.CONFIDENCE_TOO_LOW,
                        "Confidence " + requirement.dimension().name() + " is unknown; required >= "
                                + requirement.minimum());
            } else if (!sample.meets(requirement.minimum())) {
                builder.reject(FailureReason.CONFIDENCE_TOO_LOW,
                        "Confidence " + requirement.dimension().name() + " is " + sample
                                + "; required >= " + requirement.minimum());
            } else {
                builder.note("Confidence " + requirement.dimension().name() + " " + sample
                        + " meets " + requirement.minimum());
            }
        }
    }

    private void evaluateResources(Task task, WorldSnapshot snapshot, TaskEvaluation.Builder builder) {
        for (Resource resource : task.requiredResources()) {
            if (!snapshot.resourceKnown(resource)) {
                builder.reject(FailureReason.RESOURCE_CONFLICT,
                        "Resource " + resource.name() + " availability is unknown");
            } else if (!snapshot.resourceAvailable(resource)) {
                builder.reject(FailureReason.RESOURCE_CONFLICT,
                        "Resource " + resource.name() + " is not available");
            }
        }
    }

    private void evaluateConditions(Task task, WorldSnapshot snapshot, TaskEvaluation.Builder builder) {
        for (Condition condition : task.preconditions()) {
            ConditionResult result = condition.evaluate(snapshot);
            builder.note("Precondition " + condition.name() + "=" + result.value()
                    + " (" + result.explanation() + ")");
            if (result.value() == ConditionValue.UNKNOWN) {
                builder.reject(FailureReason.UNKNOWN_CONDITION,
                        "Precondition '" + condition.name() + "' is UNKNOWN");
            } else if (result.value() == ConditionValue.STALE) {
                builder.reject(FailureReason.STALE_INPUT,
                        "Precondition '" + condition.name() + "' is STALE");
            } else if (result.value() == ConditionValue.FALSE) {
                builder.reject(FailureReason.PRECONDITION_UNMET,
                        "Precondition '" + condition.name() + "' is FALSE");
            }
        }
    }

    private void evaluateTime(Task task, WorldSnapshot snapshot, TaskEvaluation.Builder builder) {
        Duration minimum = task.minimumRemainingTime().orElse(null);
        if (minimum == null) {
            return;
        }
        if (!snapshot.remainingAutonomous().isKnown() && !snapshot.remainingMatch().isKnown()) {
            builder.reject(FailureReason.INSUFFICIENT_TIME,
                    "Remaining time is unknown; task requires at least " + minimum.toMillis() + " ms");
            return;
        }
        boolean autoOk = !snapshot.remainingAutonomous().isKnown()
                || snapshot.remainingAutonomous().hasAtLeast(minimum);
        boolean matchOk = !snapshot.remainingMatch().isKnown()
                || snapshot.remainingMatch().hasAtLeast(minimum);
        if (!autoOk || !matchOk) {
            builder.reject(FailureReason.INSUFFICIENT_TIME,
                    "Remaining time is below the required " + minimum.toMillis() + " ms margin");
        }
    }
}
