package org.allsparks.helm.resource;

import java.util.Objects;

/**
 * A request to use a resource for a named owner (task or intent node).
 */
public final class ResourceClaim {
    private final Resource resource;
    private final String owner;
    private final int priority;

    public ResourceClaim(Resource resource, String owner, int priority) {
        this.resource = Objects.requireNonNull(resource, "resource");
        this.owner = requireText(owner, "owner");
        this.priority = priority;
    }

    public Resource resource() {
        return resource;
    }

    public String owner() {
        return owner;
    }

    public int priority() {
        return priority;
    }

    private static String requireText(String value, String field) {
        Objects.requireNonNull(value, field);
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return trimmed;
    }

    @Override
    public String toString() {
        return owner + "->" + resource + "@" + priority;
    }
}
