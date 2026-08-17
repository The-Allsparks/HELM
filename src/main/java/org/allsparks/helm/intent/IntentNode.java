package org.allsparks.helm.intent;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.allsparks.helm.resource.Resource;
import org.allsparks.helm.task.RetryPolicy;
import org.allsparks.helm.task.TimeoutPolicy;

/**
 * Immutable intent-tree node. This is vocabulary and validation structure, not
 * a hardware executor.
 */
public final class IntentNode {
    private final String name;
    private final IntentNodeKind kind;
    private final List<IntentNode> children;
    private final Optional<TimeoutPolicy> timeout;
    private final Optional<RetryPolicy> retryPolicy;
    private final List<Resource> resources;
    private final Optional<String> subtreeName;
    private final Optional<Duration> waitDuration;

    private IntentNode(Builder builder) {
        this.name = requireText(builder.name, "name");
        this.kind = Objects.requireNonNull(builder.kind, "kind");
        this.children = Collections.unmodifiableList(List.copyOf(builder.children));
        this.timeout = Optional.ofNullable(builder.timeout);
        this.retryPolicy = Optional.ofNullable(builder.retryPolicy);
        this.resources = Collections.unmodifiableList(List.copyOf(builder.resources));
        this.subtreeName = Optional.ofNullable(builder.subtreeName).filter(s -> !s.isBlank());
        this.waitDuration = Optional.ofNullable(builder.waitDuration);
    }

    public static IntentNode action(String name) {
        return builder(name, IntentNodeKind.ACTION).build();
    }

    public static IntentNode condition(String name) {
        return builder(name, IntentNodeKind.CONDITION).build();
    }

    public static IntentNode succeed(String name) {
        return builder(name, IntentNodeKind.SUCCEED).build();
    }

    public static IntentNode fail(String name) {
        return builder(name, IntentNodeKind.FAIL).build();
    }

    public static IntentNode waitFor(String name, Duration duration) {
        return builder(name, IntentNodeKind.WAIT).waitDuration(duration).build();
    }

    public static IntentNode sequence(IntentNode... children) {
        return sequence("sequence", children);
    }

    public static IntentNode sequence(String name, IntentNode... children) {
        return builder(name, IntentNodeKind.SEQUENCE).children(children).build();
    }

    public static IntentNode fallback(IntentNode... children) {
        return fallback("fallback", children);
    }

    public static IntentNode fallback(String name, IntentNode... children) {
        return builder(name, IntentNodeKind.FALLBACK).children(children).build();
    }

    public static IntentNode parallel(IntentNode... children) {
        return parallel("parallel", children);
    }

    public static IntentNode parallel(String name, IntentNode... children) {
        return builder(name, IntentNodeKind.PARALLEL).children(children).build();
    }

    public static IntentNode timeout(String name, Duration duration, IntentNode child) {
        return builder(name, IntentNodeKind.TIMEOUT)
                .timeout(TimeoutPolicy.of(duration))
                .child(child)
                .build();
    }

    public static IntentNode retry(String name, RetryPolicy policy, IntentNode child) {
        return builder(name, IntentNodeKind.RETRY)
                .retryPolicy(policy)
                .child(child)
                .build();
    }

    public static IntentNode recovery(String name, IntentNode primary, IntentNode recovery) {
        return builder(name, IntentNodeKind.RECOVERY)
                .child(primary)
                .child(recovery)
                .build();
    }

    public static IntentNode guard(String name, IntentNode condition, IntentNode child) {
        return builder(name, IntentNodeKind.GUARD)
                .child(condition)
                .child(child)
                .build();
    }

    public static IntentNode subtree(String name) {
        return builder(name, IntentNodeKind.SUBTREE).subtreeName(name).build();
    }

    public static Builder builder(String name, IntentNodeKind kind) {
        return new Builder(name, kind);
    }

    public String name() {
        return name;
    }

    public IntentNodeKind kind() {
        return kind;
    }

    public List<IntentNode> children() {
        return children;
    }

    public Optional<TimeoutPolicy> timeout() {
        return timeout;
    }

    public Optional<RetryPolicy> retryPolicy() {
        return retryPolicy;
    }

    public List<Resource> resources() {
        return resources;
    }

    public Optional<String> subtreeName() {
        return subtreeName;
    }

    public Optional<Duration> waitDuration() {
        return waitDuration;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    private static String requireText(String text, String field) {
        Objects.requireNonNull(text, field);
        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return trimmed;
    }

    public static final class Builder {
        private final String name;
        private final IntentNodeKind kind;
        private final List<IntentNode> children = new ArrayList<>();
        private TimeoutPolicy timeout;
        private RetryPolicy retryPolicy;
        private final List<Resource> resources = new ArrayList<>();
        private String subtreeName;
        private Duration waitDuration;

        private Builder(String name, IntentNodeKind kind) {
            this.name = name;
            this.kind = kind;
        }

        public Builder child(IntentNode child) {
            this.children.add(Objects.requireNonNull(child, "child"));
            return this;
        }

        public Builder children(IntentNode... children) {
            for (IntentNode child : children) {
                child(child);
            }
            return this;
        }

        public Builder timeout(TimeoutPolicy timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder retryPolicy(RetryPolicy retryPolicy) {
            this.retryPolicy = retryPolicy;
            return this;
        }

        public Builder resource(Resource resource) {
            this.resources.add(Objects.requireNonNull(resource, "resource"));
            return this;
        }

        public Builder subtreeName(String subtreeName) {
            this.subtreeName = subtreeName;
            return this;
        }

        public Builder waitDuration(Duration waitDuration) {
            this.waitDuration = waitDuration;
            return this;
        }

        public IntentNode build() {
            return new IntentNode(this);
        }
    }
}
