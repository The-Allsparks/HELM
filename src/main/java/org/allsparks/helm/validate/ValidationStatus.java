package org.allsparks.helm.validate;

/**
 * Whether offline plan validation ran and what it concluded.
 */
public enum ValidationStatus {
    /** Validation was skipped (mode or flags). */
    NOT_RUN,
    /** Validation ran and found no errors. */
    VALID,
    /** Validation ran and found at least one error. */
    INVALID
}
