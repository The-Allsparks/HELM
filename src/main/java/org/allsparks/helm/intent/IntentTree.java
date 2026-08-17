package org.allsparks.helm.intent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Named inspectable intent tree. This scaffold stores and validates trees; it
 * does not issue robot actions.
 */
public final class IntentTree {
    private final String name;
    private final IntentNode root;
    private final Map<String, IntentNode> subtrees;

    private IntentTree(String name, IntentNode root, Map<String, IntentNode> subtrees) {
        this.name = requireText(name, "name");
        this.root = Objects.requireNonNull(root, "root");
        this.subtrees = Collections.unmodifiableMap(new LinkedHashMap<>(subtrees));
    }

    public static NamedBuilder named(String name) {
        return new NamedBuilder(name);
    }

    public String name() {
        return name;
    }

    public IntentNode root() {
        return root;
    }

    public Map<String, IntentNode> subtrees() {
        return subtrees;
    }

    public static IntentNode action(String name) {
        return IntentNode.action(name);
    }

    public static IntentNode safeTerminal(String name) {
        return IntentNode.safeTerminal(name);
    }

    public static IntentNode condition(String name) {
        return IntentNode.condition(name);
    }

    public static IntentNode fallback(IntentNode... children) {
        return IntentNode.fallback(children);
    }

    public static IntentNode sequence(IntentNode... children) {
        return IntentNode.sequence(children);
    }

    public static IntentNode parallel(IntentNode... children) {
        return IntentNode.parallel(children);
    }

    public List<IntentNode> preorder() {
        List<IntentNode> nodes = new ArrayList<>();
        walk(root, nodes);
        return Collections.unmodifiableList(nodes);
    }

    public int depth() {
        return depth(root);
    }

    public int nodeCount() {
        return preorder().size();
    }

    private static void walk(IntentNode node, List<IntentNode> nodes) {
        nodes.add(node);
        for (IntentNode child : node.children()) {
            walk(child, nodes);
        }
    }

    private static int depth(IntentNode node) {
        int max = 0;
        for (IntentNode child : node.children()) {
            max = Math.max(max, depth(child));
        }
        return 1 + max;
    }

    private static String requireText(String text, String field) {
        Objects.requireNonNull(text, field);
        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return trimmed;
    }

    public static final class NamedBuilder {
        private final String name;
        private final Map<String, IntentNode> subtrees = new LinkedHashMap<>();

        private NamedBuilder(String name) {
            this.name = name;
        }

        public NamedBuilder subtree(String subtreeName, IntentNode root) {
            this.subtrees.put(subtreeName, Objects.requireNonNull(root, "root"));
            return this;
        }

        public IntentTree sequence(IntentNode... children) {
            return root(IntentNode.sequence(name, children));
        }

        public IntentTree fallback(IntentNode... children) {
            return root(IntentNode.fallback(name, children));
        }

        public IntentTree parallel(IntentNode... children) {
            return root(IntentNode.parallel(name, children));
        }

        public IntentTree root(IntentNode root) {
            return new IntentTree(name, root, subtrees);
        }
    }
}
