package org.allsparks.helm.validate;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.allsparks.helm.HelmConfig;
import org.allsparks.helm.intent.IntentNode;
import org.allsparks.helm.intent.IntentNodeKind;
import org.allsparks.helm.intent.IntentTree;
import org.allsparks.helm.outcome.FailureReason;
import org.allsparks.helm.resource.Resource;
import org.allsparks.helm.task.Task;

/**
 * Offline/static validator. Warns or rejects; never substitutes another plan.
 */
public final class PlanValidator {
    private final HelmConfig config;

    public PlanValidator(HelmConfig config) {
        this.config = config;
    }

    public ValidationReport validate(Task task) {
        List<ValidationFinding> findings = new ArrayList<>();
        if (task.timeout().isEmpty()) {
            findings.add(error(FailureReason.MISSING_TIMEOUT, task.name(),
                    "Task '" + task.name() + "' has no timeout"));
        }
        if (task.fallbackTaskName().isEmpty()) {
            findings.add(error(FailureReason.MISSING_FALLBACK, task.name(),
                    "Task '" + task.name() + "' has no fallback"));
        }
        if (task.completionConditions().isEmpty()) {
            findings.add(error(FailureReason.INVALID_PLAN, task.name(),
                    "Task '" + task.name() + "' has no completion condition"));
        }
        if (task.requiredCapabilities().isEmpty()) {
            findings.add(warning(FailureReason.INVALID_PLAN, task.name(),
                    "Task '" + task.name() + "' declares no required capabilities"));
        }
        return new ValidationReport(task.name(), findings);
    }

    public ValidationReport validate(IntentTree tree) {
        List<ValidationFinding> findings = new ArrayList<>();
        if (tree.nodeCount() > config.maxTreeNodes()) {
            findings.add(error(FailureReason.INVALID_PLAN, tree.name(),
                    "Tree exceeds max nodes " + config.maxTreeNodes()));
        }
        if (tree.depth() > config.maxTreeDepth()) {
            findings.add(error(FailureReason.INVALID_PLAN, tree.name(),
                    "Tree exceeds max depth " + config.maxTreeDepth()));
        }
        Set<String> reachable = new HashSet<>();
        walk(tree.root(), tree.name(), tree.subtrees(), new ArrayDeque<>(), reachable, findings);
        for (Map.Entry<String, IntentNode> entry : tree.subtrees().entrySet()) {
            if (!reachable.contains(entry.getKey())) {
                findings.add(warning(FailureReason.INVALID_PLAN, entry.getKey(),
                        "Subtree '" + entry.getKey() + "' is not reachable from the root"));
            }
        }
        if (!hasSafeTerminal(tree.root())) {
            findings.add(error(FailureReason.MISSING_FALLBACK, tree.name(),
                    "Plan has no safe terminal in a structurally terminal fallback or recovery position"));
        }
        return new ValidationReport(tree.name(), findings);
    }

    private void walk(
            IntentNode node,
            String path,
            Map<String, IntentNode> subtrees,
            ArrayDeque<String> subtreeStack,
            Set<String> reachable,
            List<ValidationFinding> findings) {
        String here = path + "/" + node.name();
        if (node.kind() == IntentNodeKind.ACTION && node.timeout().isEmpty()) {
            findings.add(error(FailureReason.MISSING_TIMEOUT, here,
                    "Action '" + node.name() + "' has no timeout"));
        }
        if (node.kind() == IntentNodeKind.RETRY && node.retryPolicy().isEmpty()) {
            findings.add(error(FailureReason.UNBOUNDED_RETRY, here,
                    "Retry node '" + node.name() + "' has no bounded retry policy"));
        }
        if (node.kind() == IntentNodeKind.RECOVERY && node.children().size() < 2) {
            findings.add(error(FailureReason.MISSING_FALLBACK, here,
                    "Recovery node '" + node.name() + "' needs a primary and a fallback child"));
        }
        if ((node.kind() == IntentNodeKind.SEQUENCE
                || node.kind() == IntentNodeKind.FALLBACK
                || node.kind() == IntentNodeKind.PARALLEL)
                && node.children().isEmpty()) {
            findings.add(error(FailureReason.INVALID_PLAN, here,
                    node.kind() + " node '" + node.name() + "' has no children"));
        }
        detectResourceConflicts(node, here, findings);
        if (node.kind() == IntentNodeKind.SUBTREE) {
            String subtreeName = node.subtreeName().orElse(node.name());
            reachable.add(subtreeName);
            if (subtreeStack.contains(subtreeName)) {
                findings.add(error(FailureReason.INVALID_PLAN, here,
                        "Cyclic subtree reference involving '" + subtreeName + "'"));
                return;
            }
            IntentNode target = subtrees.get(subtreeName);
            if (target == null) {
                findings.add(error(FailureReason.INVALID_PLAN, here,
                        "Subtree '" + subtreeName + "' is not defined"));
                return;
            }
            subtreeStack.addLast(subtreeName);
            walk(target, here, subtrees, subtreeStack, reachable, findings);
            subtreeStack.removeLast();
            return;
        }
        for (IntentNode child : node.children()) {
            walk(child, here, subtrees, subtreeStack, reachable, findings);
        }
    }

    private void detectResourceConflicts(IntentNode node, String path, List<ValidationFinding> findings) {
        if (node.kind() != IntentNodeKind.PARALLEL) {
            return;
        }
        Set<Resource> exclusive = new LinkedHashSet<>();
        for (IntentNode child : node.children()) {
            for (Resource resource : collectExclusive(child)) {
                if (!exclusive.add(resource)) {
                    findings.add(error(FailureReason.RESOURCE_CONFLICT, path,
                            "Parallel node claims exclusive resource '" + resource.name()
                                    + "' more than once"));
                }
            }
        }
    }

    private List<Resource> collectExclusive(IntentNode node) {
        List<Resource> found = new ArrayList<>();
        for (Resource resource : node.resources()) {
            if (resource.exclusive()) {
                found.add(resource);
            }
        }
        for (IntentNode child : node.children()) {
            found.addAll(collectExclusive(child));
        }
        return found;
    }

    private boolean hasSafeTerminal(IntentNode node) {
        return isSafeTerminalPosition(node, false);
    }

    private boolean isSafeTerminalPosition(IntentNode node, boolean insideFallbackOrRecovery) {
        if (node.kind() == IntentNodeKind.ACTION) {
            return insideFallbackOrRecovery && node.isSafeTerminal();
        }
        if (node.kind() == IntentNodeKind.FALLBACK || node.kind() == IntentNodeKind.RECOVERY) {
            List<IntentNode> children = node.children();
            return !children.isEmpty()
                    && isSafeTerminalPosition(children.get(children.size() - 1), true);
        }
        if (node.kind() == IntentNodeKind.TIMEOUT
                || node.kind() == IntentNodeKind.RETRY
                || node.kind() == IntentNodeKind.DECORATOR) {
            return node.children().size() == 1
                    && isSafeTerminalPosition(node.children().get(0), insideFallbackOrRecovery);
        }
        if (node.kind() == IntentNodeKind.GUARD) {
            List<IntentNode> children = node.children();
            return children.size() >= 2
                    && isSafeTerminalPosition(children.get(children.size() - 1), insideFallbackOrRecovery);
        }
        return false;
    }

    private static ValidationFinding error(FailureReason reason, String path, String message) {
        return new ValidationFinding(ValidationSeverity.ERROR, reason, path, message);
    }

    private static ValidationFinding warning(FailureReason reason, String path, String message) {
        return new ValidationFinding(ValidationSeverity.WARNING, reason, path, message);
    }
}
