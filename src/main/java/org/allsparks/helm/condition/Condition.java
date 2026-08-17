package org.allsparks.helm.condition;

import java.util.Objects;
import java.util.function.Function;

import org.allsparks.helm.snapshot.WorldSnapshot;

/**
 * Named condition evaluated against a world snapshot. Conditions never query
 * hardware.
 */
public final class Condition {
    private final String name;
    private final Function<WorldSnapshot, ConditionResult> evaluator;

    private Condition(String name, Function<WorldSnapshot, ConditionResult> evaluator) {
        this.name = requireText(name, "name");
        this.evaluator = Objects.requireNonNull(evaluator, "evaluator");
    }

    public static Condition named(String name, Function<WorldSnapshot, ConditionResult> evaluator) {
        return new Condition(name, evaluator);
    }

    /**
     * Looks up a named boolean-like fact in the snapshot. Missing facts are
     * {@link ConditionValue#UNKNOWN}, never false.
     */
    public static Condition snapshotFact(String name) {
        return named(name, snapshot -> snapshot.condition(name).orElseGet(
                () -> ConditionResult.unknown(name, "world-snapshot", snapshot.timestampNanos(),
                        "Condition '" + name + "' was not present in the snapshot")));
    }

    public String name() {
        return name;
    }

    public ConditionResult evaluate(WorldSnapshot snapshot) {
        Objects.requireNonNull(snapshot, "snapshot");
        ConditionResult result = evaluator.apply(snapshot);
        if (result == null) {
            return ConditionResult.unknown(name, "condition", snapshot.timestampNanos(),
                    "Evaluator returned null");
        }
        return result;
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
