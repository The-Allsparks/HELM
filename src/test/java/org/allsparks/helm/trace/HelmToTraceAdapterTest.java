package org.allsparks.helm.trace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class HelmToTraceAdapterTest {
    @Test
    void mapsEventTypeOntoHelmChannel() {
        List<String> names = new ArrayList<>();
        List<String> messages = new ArrayList<>();
        HelmToTraceAdapter adapter = new HelmToTraceAdapter((name, message) -> {
            names.add(name);
            messages.add(message);
        });
        adapter.record(TraceEvent.of("eligibility", 10L, "snap-1", 3L).with("task", "intake"));
        assertEquals("HELM/eligibility", names.get(0));
        assertTrue(messages.get(0).contains("snap-1"));
        assertTrue(messages.get(0).contains("task=intake"));
        assertFalse(adapter.isNoOp());
        assertFalse(adapter.isValidated());
        assertEquals("Event", HelmToTraceAdapter.sanitize(""));
        assertEquals("H2go", HelmToTraceAdapter.sanitize("2go"));
    }
}
