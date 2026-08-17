package org.allsparks.helm.outcome;

/**
 * Terminal or intermediate result of a stated intent, task, or simulated node.
 */
public enum Outcome {
    SUCCEEDED,
    FAILED,
    CANCELLED,
    TIMED_OUT,
    PREEMPTED,
    BLOCKED,
    UNAVAILABLE,
    IN_PROGRESS,
    NOT_STARTED
}
