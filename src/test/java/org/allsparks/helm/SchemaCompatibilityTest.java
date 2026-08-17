package org.allsparks.helm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SchemaCompatibilityTest {
    @Test
    void schemaAndLibraryVersionsAreStableForReplayRecords() {
        assertEquals("0.1.0", HelmConfig.SCHEMA_VERSION);
        assertEquals("0.1.0-SNAPSHOT", HelmConfig.HELM_VERSION);
    }
}
