package org.allsparks.helm.snapshot;

import java.util.Objects;

import org.allsparks.helm.confidence.Confidence;

/**
 * Whether the robot currently holds a game piece. Unknown is not empty.
 */
public final class HeldGamePiece {
    private final boolean known;
    private final boolean held;
    private final String classification;
    private final Confidence possessionConfidence;
    private final long timestampNanos;

    private HeldGamePiece(
            boolean known,
            boolean held,
            String classification,
            Confidence possessionConfidence,
            long timestampNanos) {
        this.known = known;
        this.held = held;
        this.classification = classification;
        this.possessionConfidence = possessionConfidence;
        this.timestampNanos = timestampNanos;
    }

    public static HeldGamePiece unknown(long timestampNanos) {
        return new HeldGamePiece(false, false, "unknown", Confidence.unknown(), timestampNanos);
    }

    public static HeldGamePiece held(String classification, Confidence confidence, long timestampNanos) {
        return new HeldGamePiece(true, true, classification, confidence, timestampNanos);
    }

    public static HeldGamePiece empty(Confidence confidence, long timestampNanos) {
        return new HeldGamePiece(true, false, "none", confidence, timestampNanos);
    }

    public boolean isKnown() {
        return known;
    }

    public boolean isHeld() {
        return known && held;
    }

    public String classification() {
        return classification;
    }

    public Confidence possessionConfidence() {
        return possessionConfidence;
    }

    public long timestampNanos() {
        return timestampNanos;
    }

    @Override
    public String toString() {
        if (!known) {
            return "unknown";
        }
        return held ? "held:" + classification : "empty";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HeldGamePiece)) {
            return false;
        }
        HeldGamePiece that = (HeldGamePiece) o;
        return known == that.known
                && held == that.held
                && timestampNanos == that.timestampNanos
                && Objects.equals(classification, that.classification)
                && Objects.equals(possessionConfidence, that.possessionConfidence);
    }

    @Override
    public int hashCode() {
        return Objects.hash(known, held, classification, possessionConfidence, timestampNanos);
    }
}
