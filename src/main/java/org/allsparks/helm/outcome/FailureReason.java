package org.allsparks.helm.outcome;

import java.util.Objects;

/**
 * Structured failure classification. Operationally different outcomes stay
 * distinct; they are not collapsed into a generic failure.
 */
public final class FailureReason {
    public static final FailureReason NONE = named("NONE", "No failure");
    public static final FailureReason PRECONDITION_UNMET = named("PRECONDITION_UNMET", "A required precondition was not true");
    public static final FailureReason UNKNOWN_CONDITION = named("UNKNOWN_CONDITION", "A required condition was unknown");
    public static final FailureReason STALE_INPUT = named("STALE_INPUT", "A required input was stale");
    public static final FailureReason CAPABILITY_UNAVAILABLE = named("CAPABILITY_UNAVAILABLE", "A required capability was not available");
    public static final FailureReason CAPABILITY_UNKNOWN = named("CAPABILITY_UNKNOWN", "A required capability state was unknown");
    public static final FailureReason CONFIDENCE_TOO_LOW = named("CONFIDENCE_TOO_LOW", "A confidence requirement was not met");
    public static final FailureReason RESOURCE_CONFLICT = named("RESOURCE_CONFLICT", "Required resources conflicted");
    public static final FailureReason TIMEOUT = named("TIMEOUT", "The timeout elapsed");
    public static final FailureReason RETRY_EXHAUSTED = named("RETRY_EXHAUSTED", "Bounded retries were exhausted");
    public static final FailureReason SAFETY_RESTRICTION = named("SAFETY_RESTRICTION", "A lower-layer safety restriction blocked the task");
    public static final FailureReason MODE_DISABLED = named("MODE_DISABLED", "HELM mode does not allow this action");
    public static final FailureReason AUTHORITY_DENIED = named("AUTHORITY_DENIED", "Execution authority is not approved");
    public static final FailureReason ADAPTER_FAILURE = named("ADAPTER_FAILURE", "A capability adapter failed or disappeared");
    public static final FailureReason MISSING_TIMEOUT = named("MISSING_TIMEOUT", "The plan omitted a required timeout");
    public static final FailureReason MISSING_FALLBACK = named("MISSING_FALLBACK", "The plan omitted a required fallback");
    public static final FailureReason UNBOUNDED_RETRY = named("UNBOUNDED_RETRY", "The plan declared unbounded retries");
    public static final FailureReason TIME_BUDGET_EXCEEDED = named("TIME_BUDGET_EXCEEDED", "Decision evaluation exceeded its time budget");
    public static final FailureReason INSUFFICIENT_TIME = named("INSUFFICIENT_TIME", "Remaining match or auto time was too low");
    public static final FailureReason INVALID_PLAN = named("INVALID_PLAN", "The plan failed static validation");
    public static final FailureReason OPERATOR_DISABLED = named("OPERATOR_DISABLED", "The operator disable path is active");

    private final String code;
    private final String explanation;

    private FailureReason(String code, String explanation) {
        this.code = code;
        this.explanation = explanation;
    }

    public static FailureReason named(String code, String explanation) {
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(explanation, "explanation");
        String trimmed = code.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Failure reason code must not be blank");
        }
        return new FailureReason(trimmed, explanation);
    }

    public String code() {
        return code;
    }

    public String explanation() {
        return explanation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FailureReason)) {
            return false;
        }
        return code.equals(((FailureReason) o).code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    public String toString() {
        return code;
    }
}
