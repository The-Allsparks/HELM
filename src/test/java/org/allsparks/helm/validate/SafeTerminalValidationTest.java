package org.allsparks.helm.validate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.allsparks.helm.Helm;
import org.allsparks.helm.HelmConfig;
import org.allsparks.helm.authority.AuthorityGate;
import org.allsparks.helm.clock.ManualClock;
import org.allsparks.helm.intent.IntentNode;
import org.allsparks.helm.intent.IntentNodeKind;
import org.allsparks.helm.intent.IntentTree;
import org.allsparks.helm.task.TimeoutPolicy;
import org.junit.jupiter.api.Test;

class SafeTerminalValidationTest {
    private final ManualClock clock = new ManualClock();
    private final PlanValidator validator = new PlanValidator(HelmConfig.forTests(clock));

    @Test
    void markedTimeoutWrappedLastFallbackChildCanBeValid() {
        IntentTree tree = IntentTree.named("ok").root(
                IntentNode.fallback(
                        timed("ScorePreload"),
                        timeoutWrappedSafeTerminal("ParkSafely")));
        assertTrue(validator.validate(tree).isValid());
    }

    @Test
    void unmarkedParkSafelyDoesNotCountAsSafeTerminal() {
        IntentTree tree = IntentTree.named("name-only").root(
                IntentNode.fallback(
                        timed("ScorePreload"),
                        timed("ParkSafely")));
        assertFalse(validator.validate(tree).isValid());
        assertTrue(validator.validate(tree).explanation().contains("structurally terminal"));
    }

    @Test
    void unmarkedUnsafeDriveDoesNotCountAsSafeTerminal() {
        IntentTree tree = IntentTree.named("unsafe").root(
                IntentNode.fallback(
                        timed("ScorePreload"),
                        timed("UnsafeDrive")));
        assertFalse(validator.validate(tree).isValid());
    }

    @Test
    void unmarkedHoldPieceDoesNotCountAsSafeTerminal() {
        IntentTree tree = IntentTree.named("hold").root(
                IntentNode.fallback(
                        timed("ScorePreload"),
                        timed("HoldPiece")));
        assertFalse(validator.validate(tree).isValid());
    }

    @Test
    void sequenceRootWithMarkedPrefixIsInvalid() {
        IntentTree tree = IntentTree.named("prefix").sequence(
                timeoutWrappedSafeTerminal("ParkSafely"),
                timed("ScorePreload"));
        assertFalse(validator.validate(tree).isValid());
    }

    @Test
    void fallbackWhoseLastChildSequenceHasMarkedPrefixIsInvalid() {
        IntentTree tree = IntentTree.named("seq-last").root(
                IntentNode.fallback(
                        timed("ScorePreload"),
                        IntentNode.sequence(
                                timeoutWrappedSafeTerminal("ParkSafely"),
                                timed("Other"))));
        assertFalse(validator.validate(tree).isValid());
    }

    @Test
    void nestedRecoveryAsLastFallbackChildCanBeValid() {
        IntentTree tree = IntentTree.named("recovery").root(
                IntentNode.fallback(
                        timed("ScorePreload"),
                        IntentNode.recovery(
                                "recover",
                                timed("RetryPark"),
                                timeoutWrappedSafeTerminal("ParkSafely"))));
        assertTrue(validator.validate(tree).isValid());
    }

    @Test
    void sequenceRootMatchingLegacyStudentExampleIsInvalidEvenWhenMarked() {
        IntentTree tree = IntentTree.named("SimpleAutonomous")
                .sequence(
                        IntentTree.condition("PreflightReady"),
                        timed("ScorePreload"),
                        IntentTree.fallback(
                                timed("AcquireNearestPiece"),
                                timeoutWrappedSafeTerminal("ParkSafely")));
        assertFalse(validator.validate(tree).isValid());
    }

    @Test
    void loneMarkedActionIsNotAPlanTerminal() {
        IntentTree tree = IntentTree.named("park-only").root(timedSafeTerminal("ParkSafely"));
        assertFalse(validator.validate(tree).isValid());
    }

    @Test
    void safeTerminalFactoryMarksActionAndRejectsNonActionBuilder() {
        assertTrue(IntentNode.safeTerminal("ParkSafely").isSafeTerminal());
        assertFalse(IntentNode.action("ParkSafely").isSafeTerminal());
        assertThrows(IllegalArgumentException.class, () -> IntentNode.builder("fb", IntentNodeKind.FALLBACK)
                .safeTerminal(true)
                .build());
    }

    @Test
    void physicalOutputRemainsRefused() {
        assertFalse(new AuthorityGate(HelmConfig.forTests(clock)).allowsPhysicalOutput());
        assertFalse(Helm.create().allowsPhysicalOutput());
    }

    private static IntentNode timed(String name) {
        return IntentNode.builder(name, IntentNodeKind.ACTION)
                .timeout(TimeoutPolicy.ofSeconds(2))
                .build();
    }

    private static IntentNode timedSafeTerminal(String name) {
        return IntentNode.builder(name, IntentNodeKind.ACTION)
                .safeTerminal(true)
                .timeout(TimeoutPolicy.ofSeconds(2))
                .build();
    }

    private static IntentNode timeoutWrappedSafeTerminal(String name) {
        return IntentNode.timeout("parkTimeout", Duration.ofSeconds(2), timedSafeTerminal(name));
    }
}
