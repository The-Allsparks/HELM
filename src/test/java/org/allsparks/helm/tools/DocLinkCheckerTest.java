package org.allsparks.helm.tools;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

class DocLinkCheckerTest {
    @Test
    void requiredDocumentationExists() {
        List<String> required = List.of(
                "README.md",
                "LICENSE",
                "CONTRIBUTING.md",
                "SECURITY.md",
                "CODE_OF_CONDUCT.md",
                "docs/architecture.md",
                "docs/responsibility-boundaries.md",
                "docs/world-snapshot.md",
                "docs/goals-and-tasks.md",
                "docs/intent-trees.md",
                "docs/conditions-and-confidence.md",
                "docs/capabilities.md",
                "docs/resources.md",
                "docs/recovery.md",
                "docs/decision-explanations.md",
                "docs/trace-integration.md",
                "docs/replay.md",
                "docs/season-strategy.md",
                "docs/performance.md",
                "docs/safety.md",
                "docs/readiness-gates.md",
                "docs/student-learning-path.md",
                "docs/mentor-guide.md",
                "docs/troubleshooting.md",
                "docs/research/ecosystem-review.md",
                "docs/research/source-register.md",
                "docs/research/build-vs-adopt.md",
                "docs/research/team-code-case-studies.md",
                "docs/adr/0001-build-versus-adopt.md",
                "docs/adr/0015-authority-gates.md",
                "docs/audits/initial-deep-audit.md",
                "docs/priority-ledger.md"
        );
        Path root = Path.of("").toAbsolutePath();
        if (!Files.exists(root.resolve("README.md"))) {
            root = root.getParent();
        }
        Path base = Files.exists(root.resolve("README.md")) ? root : Path.of(".");
        for (String relative : required) {
            assertTrue(Files.exists(base.resolve(relative)), "Missing " + relative);
        }
    }
}
