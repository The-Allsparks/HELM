package org.allsparks.helm.sim;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.allsparks.helm.clock.HelmClock;
import org.allsparks.helm.condition.ConditionValue;
import org.allsparks.helm.intent.IntentNode;
import org.allsparks.helm.intent.IntentStatus;
import org.allsparks.helm.intent.IntentTree;

/**
 * Deterministic desktop walker for intent trees. It never calls hardware
 * adapters and must not be used as robot execution.
 */
public final class SimulatedTreeWalker {
    private final HelmClock clock;
    private final SimulatedWorld world;
    private final Map<IntentNode, NodeState> states = new LinkedHashMap<>();
    private final ArrayDeque<String> subtreeStack = new ArrayDeque<>();
    private Map<String, IntentNode> subtrees = Map.of();
    private boolean cancelled;
    private boolean preempted;

    public SimulatedTreeWalker(HelmClock clock, SimulatedWorld world) {
        this.clock = Objects.requireNonNull(clock, "clock");
        this.world = Objects.requireNonNull(world, "world");
    }

    public IntentStatus tick(IntentTree tree) {
        if (cancelled) {
            return IntentStatus.CANCELLED;
        }
        if (preempted) {
            return IntentStatus.PREEMPTED;
        }
        this.subtrees = tree.subtrees();
        subtreeStack.clear();
        try {
            return tickNode(tree.root());
        } finally {
            this.subtrees = Map.of();
            subtreeStack.clear();
        }
    }

    public void cancel() {
        cancelled = true;
        for (NodeState state : states.values()) {
            state.status = IntentStatus.CANCELLED;
        }
    }

    public void preempt() {
        preempted = true;
        for (NodeState state : states.values()) {
            state.status = IntentStatus.PREEMPTED;
        }
    }

    public IntentStatus statusOf(IntentNode node) {
        NodeState state = states.get(node);
        return state == null ? IntentStatus.READY : state.status;
    }

    private IntentStatus tickNode(IntentNode node) {
        if (cancelled) {
            return remember(node, IntentStatus.CANCELLED);
        }
        if (preempted) {
            return remember(node, IntentStatus.PREEMPTED);
        }
        NodeState state = states.computeIfAbsent(node, ignored -> new NodeState(clock.nanoTime()));
        if (state.status != IntentStatus.READY
                && state.status != IntentStatus.RUNNING
                && isTerminal(state.status)) {
            return state.status;
        }
        switch (node.kind()) {
            case SUCCEED:
                return remember(node, IntentStatus.SUCCEEDED);
            case FAIL:
                return remember(node, IntentStatus.FAILED);
            case WAIT:
                long waitNanos = node.waitDuration().orElseThrow().toNanos();
                if (clock.nanoTime() - state.startedAtNanos >= waitNanos) {
                    return remember(node, IntentStatus.SUCCEEDED);
                }
                return remember(node, IntentStatus.RUNNING);
            case CONDITION:
                return remember(node, conditionStatus(world.conditionValue(node.name())));
            case ACTION:
                return tickAction(node, state);
            case SEQUENCE:
                return tickSequence(node, state);
            case FALLBACK:
                return tickFallback(node, state);
            case PARALLEL:
                return tickParallel(node);
            case TIMEOUT:
                return tickTimeout(node, state);
            case RETRY:
                return tickRetry(node, state);
            case GUARD:
                return tickGuard(node);
            case RECOVERY:
                return tickFallback(node, state);
            case DECORATOR:
                return tickDecorator(node);
            case SUBTREE:
                return tickSubtree(node);
            default:
                return remember(node, IntentStatus.UNAVAILABLE);
        }
    }

    private IntentStatus tickDecorator(IntentNode node) {
        if (node.children().isEmpty()) {
            return remember(node, IntentStatus.UNAVAILABLE);
        }
        return remember(node, tickNode(node.children().get(0)));
    }

    /**
     * Resolves {@code IntentTree.subtrees()} the same way {@code PlanValidator}
     * does. Missing or cyclic named subtrees are {@link IntentStatus#UNAVAILABLE},
     * never success. This walker still never calls hardware adapters.
     */
    private IntentStatus tickSubtree(IntentNode node) {
        String subtreeName = node.subtreeName().orElse(node.name());
        if (subtreeStack.contains(subtreeName)) {
            return remember(node, IntentStatus.UNAVAILABLE);
        }
        IntentNode target = subtrees.get(subtreeName);
        if (target == null) {
            return remember(node, IntentStatus.UNAVAILABLE);
        }
        subtreeStack.addLast(subtreeName);
        try {
            return remember(node, tickNode(target));
        } finally {
            subtreeStack.removeLast();
        }
    }

    private IntentStatus tickAction(IntentNode node, NodeState state) {
        if (node.timeout().isPresent()) {
            long limit = node.timeout().get().duration().toNanos();
            if (clock.nanoTime() - state.startedAtNanos >= limit) {
                return remember(node, IntentStatus.TIMED_OUT);
            }
        }
        return remember(node, world.actionStatus(node.name()));
    }

    private IntentStatus tickSequence(IntentNode node, NodeState state) {
        List<IntentNode> children = node.children();
        while (state.childIndex < children.size()) {
            IntentStatus child = tickNode(children.get(state.childIndex));
            if (child == IntentStatus.RUNNING || child == IntentStatus.READY) {
                return remember(node, IntentStatus.RUNNING);
            }
            if (child == IntentStatus.SUCCEEDED) {
                state.childIndex++;
                continue;
            }
            return remember(node, child);
        }
        return remember(node, IntentStatus.SUCCEEDED);
    }

    private IntentStatus tickFallback(IntentNode node, NodeState state) {
        List<IntentNode> children = node.children();
        while (state.childIndex < children.size()) {
            IntentStatus child = tickNode(children.get(state.childIndex));
            if (child == IntentStatus.RUNNING || child == IntentStatus.READY) {
                return remember(node, IntentStatus.RUNNING);
            }
            if (child == IntentStatus.SUCCEEDED) {
                return remember(node, IntentStatus.SUCCEEDED);
            }
            if (child == IntentStatus.CANCELLED || child == IntentStatus.PREEMPTED) {
                return remember(node, child);
            }
            state.childIndex++;
        }
        return remember(node, IntentStatus.FAILED);
    }

    private IntentStatus tickParallel(IntentNode node) {
        boolean anyRunning = false;
        for (IntentNode child : node.children()) {
            IntentStatus status = tickNode(child);
            if (status == IntentStatus.FAILED
                    || status == IntentStatus.TIMED_OUT
                    || status == IntentStatus.CANCELLED
                    || status == IntentStatus.PREEMPTED
                    || status == IntentStatus.UNAVAILABLE
                    || status == IntentStatus.BLOCKED) {
                return remember(node, status);
            }
            if (status != IntentStatus.SUCCEEDED) {
                anyRunning = true;
            }
        }
        return remember(node, anyRunning ? IntentStatus.RUNNING : IntentStatus.SUCCEEDED);
    }

    private IntentStatus tickTimeout(IntentNode node, NodeState state) {
        long limit = node.timeout().orElseThrow().duration().toNanos();
        if (clock.nanoTime() - state.startedAtNanos >= limit) {
            return remember(node, IntentStatus.TIMED_OUT);
        }
        if (node.children().isEmpty()) {
            return remember(node, IntentStatus.UNAVAILABLE);
        }
        return remember(node, tickNode(node.children().get(0)));
    }

    private IntentStatus tickRetry(IntentNode node, NodeState state) {
        if (node.children().isEmpty() || node.retryPolicy().isEmpty()) {
            return remember(node, IntentStatus.UNAVAILABLE);
        }
        int maxAttempts = node.retryPolicy().get().maxAttempts();
        long maxDuration = node.retryPolicy().get().maxDuration().toNanos();
        if (clock.nanoTime() - state.startedAtNanos >= maxDuration) {
            return remember(node, IntentStatus.TIMED_OUT);
        }
        IntentNode child = node.children().get(0);
        IntentStatus status = tickNode(child);
        if (status == IntentStatus.SUCCEEDED) {
            return remember(node, IntentStatus.SUCCEEDED);
        }
        if (status == IntentStatus.RUNNING || status == IntentStatus.READY) {
            return remember(node, IntentStatus.RUNNING);
        }
        state.attempts++;
        if (state.attempts >= maxAttempts) {
            return remember(node, IntentStatus.FAILED);
        }
        states.remove(child);
        return remember(node, IntentStatus.RUNNING);
    }

    private IntentStatus tickGuard(IntentNode node) {
        if (node.children().size() < 2) {
            return remember(node, IntentStatus.UNAVAILABLE);
        }
        IntentStatus gate = tickNode(node.children().get(0));
        if (gate != IntentStatus.SUCCEEDED) {
            return remember(node, gate == IntentStatus.FAILED ? IntentStatus.BLOCKED : gate);
        }
        return remember(node, tickNode(node.children().get(1)));
    }

    private IntentStatus conditionStatus(ConditionValue value) {
        switch (value) {
            case TRUE:
                return IntentStatus.SUCCEEDED;
            case FALSE:
                return IntentStatus.FAILED;
            case STALE:
                return IntentStatus.BLOCKED;
            case UNKNOWN:
            default:
                return IntentStatus.UNAVAILABLE;
        }
    }

    private IntentStatus remember(IntentNode node, IntentStatus status) {
        NodeState state = states.computeIfAbsent(node, ignored -> new NodeState(clock.nanoTime()));
        state.status = status;
        return status;
    }

    private static boolean isTerminal(IntentStatus status) {
        return status == IntentStatus.SUCCEEDED
                || status == IntentStatus.FAILED
                || status == IntentStatus.CANCELLED
                || status == IntentStatus.TIMED_OUT
                || status == IntentStatus.PREEMPTED
                || status == IntentStatus.UNAVAILABLE
                || status == IntentStatus.BLOCKED;
    }

    private static final class NodeState {
        private final long startedAtNanos;
        private IntentStatus status = IntentStatus.READY;
        private int childIndex;
        private int attempts;

        private NodeState(long startedAtNanos) {
            this.startedAtNanos = startedAtNanos;
        }
    }

    public List<String> transitions() {
        List<String> names = new ArrayList<>();
        for (Map.Entry<IntentNode, NodeState> entry : states.entrySet()) {
            names.add(entry.getKey().name() + "=" + entry.getValue().status);
        }
        return names;
    }
}
