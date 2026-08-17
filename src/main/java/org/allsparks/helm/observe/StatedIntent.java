package org.allsparks.helm.observe;

import java.util.Objects;
import java.util.Optional;

/**
 * Robot application's stated intent, recorded without HELM taking control.
 */
public final class StatedIntent {
    private final String name;
    private final String source;
    private final Optional<String> goalName;

    public StatedIntent(String name, String source, String goalName) {
        this.name = requireText(name, "name");
        this.source = requireText(source, "source");
        this.goalName = Optional.ofNullable(goalName).filter(s -> !s.isBlank());
    }

    public static StatedIntent named(String name) {
        return new StatedIntent(name, "robot-application", null);
    }

    public String name() {
        return name;
    }

    public String source() {
        return source;
    }

    public Optional<String> goalName() {
        return goalName;
    }

    private static String requireText(String text, String field) {
        Objects.requireNonNull(text, field);
        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return trimmed;
    }
}
