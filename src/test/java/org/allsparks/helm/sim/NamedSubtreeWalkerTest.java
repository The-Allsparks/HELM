package org.allsparks.helm.sim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.allsparks.helm.HelmConfig;
import org.allsparks.helm.adapter.NoOpActionAdapter;
import org.allsparks.helm.authority.AuthorityGate;
import org.allsparks.helm.clock.ManualClock;
import org.allsparks.helm.intent.IntentNode;
import org.allsparks.helm.intent.IntentStatus;
import org.allsparks.helm.intent.IntentTree;
import org.junit.jupiter.api.Test;

class NamedSubtreeWalkerTest {
    private final ManualClock clock = new ManualClock();

    @Test
    void namedSequenceSubtreeSucceedsWhenLeavesSucceed() {
        IntentTree tree = IntentTree.named("root")
                .subtree("score", IntentNode.sequence(
                        IntentNode.action("one"),
                        IntentNode.action("two")))
                .root(IntentNode.subtree("score"));
        SimulatedWorld world = new SimulatedWorld()
                .action("one", IntentStatus.SUCCEEDED)
                .action("two", IntentStatus.SUCCEEDED);
        assertEquals(IntentStatus.SUCCEEDED, new SimulatedTreeWalker(clock, world).tick(tree));
    }

    @Test
    void namedSequenceSubtreeFailsWhenALeafFails() {
        IntentTree tree = IntentTree.named("root")
                .subtree("score", IntentNode.sequence(
                        IntentNode.action("one"),
                        IntentNode.action("two")))
                .root(IntentNode.subtree("score"));
        SimulatedWorld world = new SimulatedWorld()
                .action("one", IntentStatus.SUCCEEDED)
                .action("two", IntentStatus.FAILED);
        assertEquals(IntentStatus.FAILED, new SimulatedTreeWalker(clock, world).tick(tree));
    }

    @Test
    void missingSubtreeNameIsUnavailableNotSuccess() {
        IntentTree tree = IntentTree.named("missing")
                .root(IntentNode.subtree("score"));
        SimulatedWorld world = new SimulatedWorld()
                .action("score", IntentStatus.SUCCEEDED);
        assertEquals(IntentStatus.UNAVAILABLE, new SimulatedTreeWalker(clock, world).tick(tree));
    }

    @Test
    void cyclicSubtreeDoesNotInfiniteLoop() {
        IntentTree cyclic = IntentTree.named("cycle")
                .subtree("A", IntentNode.subtree("B"))
                .subtree("B", IntentNode.subtree("A"))
                .root(IntentNode.subtree("A"));
        IntentStatus status = new SimulatedTreeWalker(clock, new SimulatedWorld()).tick(cyclic);
        assertEquals(IntentStatus.UNAVAILABLE, status);
    }

    @Test
    void walkerDoesNotAllowPhysicalOutput() {
        assertFalse(new AuthorityGate(HelmConfig.defaults()).allowsPhysicalOutput());
        assertTrue(new NoOpActionAdapter("Pedro").isNoOp());
    }
}
