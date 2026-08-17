package org.allsparks.helm.observe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * In-memory execution history for Phase 1 observation. Bounded to keep Control
 * Hub allocations predictable.
 */
public final class ExecutionHistory {
    private final int capacity;
    private final ArrayList<ObservedEvent> events = new ArrayList<>();

    public ExecutionHistory(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("History capacity must be positive");
        }
        this.capacity = capacity;
    }

    public void add(ObservedEvent event) {
        if (events.size() == capacity) {
            events.remove(0);
        }
        events.add(event);
    }

    public List<ObservedEvent> events() {
        return Collections.unmodifiableList(events);
    }

    public Optional<ObservedEvent> latest() {
        if (events.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(events.get(events.size() - 1));
    }

    public int size() {
        return events.size();
    }
}
