package org.allsparks.helm.intent;

/**
 * Deterministic node status. Operationally different outcomes are not collapsed
 * into a generic failure.
 */
public enum IntentStatus {
    READY,
    RUNNING,
    SUCCEEDED,
    FAILED,
    BLOCKED,
    CANCELLED,
    TIMED_OUT,
    PREEMPTED,
    UNAVAILABLE
}
