package org.allsparks.helm.resource;

/**
 * Whether a resource may be shared. MIMIC remains responsible for physical
 * interlocks; this model only prevents logically incompatible HELM tasks.
 */
public enum ResourceKind {
    EXCLUSIVE,
    SHAREABLE
}
