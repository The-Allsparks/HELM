package org.allsparks.helm.condition;

/**
 * Four-valued condition result. Unknown and stale are not booleans.
 *
 * <p>Do not convert {@link #UNKNOWN} to false or true, and do not treat
 * {@link #STALE} as valid evidence.
 */
public enum ConditionValue {
    TRUE,
    FALSE,
    UNKNOWN,
    STALE;

    public boolean isKnownTrue() {
        return this == TRUE;
    }

    public boolean isKnownFalse() {
        return this == FALSE;
    }

    public boolean isCertain() {
        return this == TRUE || this == FALSE;
    }

    public boolean blocksCertainty() {
        return this == UNKNOWN || this == STALE;
    }
}
