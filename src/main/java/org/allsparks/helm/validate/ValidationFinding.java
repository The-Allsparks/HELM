package org.allsparks.helm.validate;

import java.util.Objects;

import org.allsparks.helm.outcome.FailureReason;

/**
 * One static validation finding. HELM may warn or reject; it does not
 * substitute another plan.
 */
public final class ValidationFinding {
    private final ValidationSeverity severity;
    private final FailureReason reason;
    private final String path;
    private final String message;

    public ValidationFinding(ValidationSeverity severity, FailureReason reason, String path, String message) {
        this.severity = Objects.requireNonNull(severity, "severity");
        this.reason = Objects.requireNonNull(reason, "reason");
        this.path = path == null ? "" : path;
        this.message = Objects.requireNonNull(message, "message");
    }

    public ValidationSeverity severity() {
        return severity;
    }

    public FailureReason reason() {
        return reason;
    }

    public String path() {
        return path;
    }

    public String message() {
        return message;
    }

    public boolean isError() {
        return severity == ValidationSeverity.ERROR;
    }
}
