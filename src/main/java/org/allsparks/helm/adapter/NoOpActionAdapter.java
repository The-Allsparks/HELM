package org.allsparks.helm.adapter;

import org.allsparks.helm.intent.IntentStatus;
import org.allsparks.helm.snapshot.WorldSnapshot;

/**
 * Adapter that never issues hardware commands. Used for tests and as the only
 * adapter shipped in this scaffold.
 */
public final class NoOpActionAdapter implements ActionAdapter {
    private final String name;

    public NoOpActionAdapter(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public IntentStatus tick(WorldSnapshot snapshot) {
        return IntentStatus.UNAVAILABLE;
    }

    @Override
    public void cancel() {
        // Nothing to cancel.
    }

    @Override
    public boolean isNoOp() {
        return true;
    }
}
