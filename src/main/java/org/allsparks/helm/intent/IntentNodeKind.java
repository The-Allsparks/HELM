package org.allsparks.helm.intent;

/**
 * Inspectable intent-tree node kinds. HELM core stores structure; it does not
 * interpret season-specific action names.
 */
public enum IntentNodeKind {
    SEQUENCE,
    FALLBACK,
    PARALLEL,
    CONDITION,
    ACTION,
    DECORATOR,
    TIMEOUT,
    RETRY,
    GUARD,
    SUBTREE,
    RECOVERY,
    WAIT,
    SUCCEED,
    FAIL
}
