package org.allsparks.helm.sim;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.allsparks.helm.condition.ConditionValue;
import org.allsparks.helm.intent.IntentStatus;

/**
 * In-memory leaf outcomes for desktop simulation. Never connected to hardware.
 */
public final class SimulatedWorld {
    private final Map<String, IntentStatus> actions = new LinkedHashMap<>();
    private final Map<String, ConditionValue> conditions = new LinkedHashMap<>();

    public SimulatedWorld action(String name, IntentStatus status) {
        actions.put(name, Objects.requireNonNull(status, "status"));
        return this;
    }

    public SimulatedWorld condition(String name, ConditionValue value) {
        conditions.put(name, Objects.requireNonNull(value, "value"));
        return this;
    }

    public IntentStatus actionStatus(String name) {
        return actions.getOrDefault(name, IntentStatus.UNAVAILABLE);
    }

    public ConditionValue conditionValue(String name) {
        return conditions.getOrDefault(name, ConditionValue.UNKNOWN);
    }

    public List<String> actionNames() {
        return new ArrayList<>(actions.keySet());
    }
}
