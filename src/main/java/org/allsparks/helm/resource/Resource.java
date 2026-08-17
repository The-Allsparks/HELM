package org.allsparks.helm.resource;

import java.util.Objects;

/**
 * Named logical resource. Well-known names are constants; robot-specific
 * resources should be defined by the application.
 */
public final class Resource {
    public static final Resource DRIVETRAIN = exclusive("DRIVETRAIN");
    public static final Resource INTAKE = exclusive("INTAKE");
    public static final Resource ELEVATOR = exclusive("ELEVATOR");
    public static final Resource ARM = exclusive("ARM");
    public static final Resource SCORING_EFFECTOR = exclusive("SCORING_EFFECTOR");
    public static final Resource VISION_AIM = exclusive("VISION_AIM");
    public static final Resource POWER_BURST = exclusive("POWER_BURST");
    public static final Resource OPERATOR_AUTHORITY = exclusive("OPERATOR_AUTHORITY");

    private final String name;
    private final ResourceKind kind;

    private Resource(String name, ResourceKind kind) {
        this.name = name;
        this.kind = kind;
    }

    public static Resource exclusive(String name) {
        return named(name, ResourceKind.EXCLUSIVE);
    }

    public static Resource shareable(String name) {
        return named(name, ResourceKind.SHAREABLE);
    }

    public static Resource named(String name, ResourceKind kind) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(kind, "kind");
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Resource name must not be blank");
        }
        return new Resource(trimmed, kind);
    }

    public String name() {
        return name;
    }

    public ResourceKind kind() {
        return kind;
    }

    public boolean exclusive() {
        return kind == ResourceKind.EXCLUSIVE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Resource)) {
            return false;
        }
        Resource resource = (Resource) o;
        return name.equals(resource.name) && kind == resource.kind;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, kind);
    }

    @Override
    public String toString() {
        return name + "/" + kind;
    }
}
