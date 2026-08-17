package org.allsparks.helm.validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Result of offline plan validation. Invalid plans are not rewritten.
 *
 * <p>When validation is skipped because mode or flags disallow it, use
 * {@link #notRun(String, String)}. {@link #isValid()} is {@code true} only when
 * validation actually ran and found no errors.
 */
public final class ValidationReport {
    private final String subject;
    private final List<ValidationFinding> findings;
    private final ValidationStatus status;
    private final String skipReason;

    public ValidationReport(String subject, List<ValidationFinding> findings) {
        this.subject = Objects.requireNonNull(subject, "subject");
        this.findings = Collections.unmodifiableList(List.copyOf(findings));
        this.status = findings.stream().anyMatch(ValidationFinding::isError)
                ? ValidationStatus.INVALID
                : ValidationStatus.VALID;
        this.skipReason = null;
    }

    private ValidationReport(String subject, ValidationStatus status, String skipReason) {
        this.subject = Objects.requireNonNull(subject, "subject");
        this.findings = List.of();
        this.status = Objects.requireNonNull(status, "status");
        this.skipReason = Objects.requireNonNull(skipReason, "skipReason");
    }

    public static ValidationReport notRun(String subject, String reason) {
        return new ValidationReport(subject, ValidationStatus.NOT_RUN, reason);
    }

    public String subject() {
        return subject;
    }

    public List<ValidationFinding> findings() {
        return findings;
    }

    public ValidationStatus status() {
        return status;
    }

    public boolean wasValidated() {
        return status != ValidationStatus.NOT_RUN;
    }

    public boolean isValid() {
        return status == ValidationStatus.VALID;
    }

    public List<ValidationFinding> errors() {
        return findings.stream().filter(ValidationFinding::isError).collect(Collectors.toUnmodifiableList());
    }

    public String explanation() {
        if (status == ValidationStatus.NOT_RUN) {
            return "Plan '" + subject + "' was not validated: " + skipReason;
        }
        if (status == ValidationStatus.VALID) {
            return "Plan '" + subject + "' is valid";
        }
        StringBuilder text = new StringBuilder("Plan '").append(subject).append("' is not safe to run: ");
        List<String> messages = new ArrayList<>();
        for (ValidationFinding finding : errors()) {
            messages.add(finding.message());
        }
        text.append(String.join("; ", messages));
        return text.toString();
    }
}
