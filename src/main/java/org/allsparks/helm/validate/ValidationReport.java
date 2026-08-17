package org.allsparks.helm.validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Result of offline plan validation. Invalid plans are not rewritten.
 */
public final class ValidationReport {
    private final String subject;
    private final List<ValidationFinding> findings;

    public ValidationReport(String subject, List<ValidationFinding> findings) {
        this.subject = Objects.requireNonNull(subject, "subject");
        this.findings = Collections.unmodifiableList(List.copyOf(findings));
    }

    public String subject() {
        return subject;
    }

    public List<ValidationFinding> findings() {
        return findings;
    }

    public boolean isValid() {
        return findings.stream().noneMatch(ValidationFinding::isError);
    }

    public List<ValidationFinding> errors() {
        return findings.stream().filter(ValidationFinding::isError).collect(Collectors.toUnmodifiableList());
    }

    public String explanation() {
        if (isValid()) {
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
