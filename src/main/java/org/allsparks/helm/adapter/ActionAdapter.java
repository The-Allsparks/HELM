package org.allsparks.helm.adapter;

import org.allsparks.helm.intent.IntentStatus;
import org.allsparks.helm.snapshot.WorldSnapshot;

/**
 * Leaf-action adapter contract. Implementations must not live in HELM core as
 * compile dependencies on Pedro, MIMIC, or command frameworks.
 *
 * <p>This scaffold ships only a no-op adapter. Real adapters are deferred.
 */
public interface ActionAdapter {
    String name();

    IntentStatus tick(WorldSnapshot snapshot);

    void cancel();

    boolean isNoOp();
}
