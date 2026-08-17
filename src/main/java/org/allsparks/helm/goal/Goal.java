package org.allsparks.helm.goal;

import java.util.Objects;

/**
 * Named desired outcome. Season scoring policy lives outside HELM core.
 */
public final class Goal {
    private final String name;
    private final String description;

    private Goal(String name, String description) {
        this.name = requireText(name, "name");
        this.description = description == null ? "" : description;
    }

    public static Goal named(String name) {
        return new Goal(name, "");
    }

    public static Goal named(String name, String description) {
        return new Goal(name, description);
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Goal)) {
            return false;
        }
        return name.equals(((Goal) o).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
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
