package org.allsparks.helm.snapshot;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.allsparks.helm.capability.Capability;
import org.allsparks.helm.capability.CapabilityAvailability;
import org.allsparks.helm.capability.CapabilityState;
import org.allsparks.helm.condition.ConditionResult;
import org.allsparks.helm.confidence.Confidence;
import org.allsparks.helm.confidence.ConfidenceDimension;
import org.allsparks.helm.resource.Resource;

/**
 * Builder for an immutable {@link WorldSnapshot}. Collections are copied on
 * {@link #build()}.
 */
public final class WorldSnapshotBuilder {
    String snapshotId;
    long timestampNanos;
    PoseEstimate pose;
    List<ObservedTarget> targets = new ArrayList<>();
    HeldGamePiece heldGamePiece;
    Map<Capability, CapabilityState> capabilities = new LinkedHashMap<>();
    Map<Resource, Boolean> resourcesAvailable = new LinkedHashMap<>();
    Map<String, ConditionResult> conditions = new LinkedHashMap<>();
    Map<ConfidenceDimension, Confidence> confidences = new LinkedHashMap<>();
    List<SafetyRestriction> safetyRestrictions = new ArrayList<>();
    RemainingTime remainingAutonomous = RemainingTime.unknown();
    RemainingTime remainingMatch = RemainingTime.unknown();
    String currentTaskName;
    String operatorStrategy;
    boolean operatorDisable;
    TimestampAlignment timestampAlignment;
    long alignmentWindowNanos = Duration.ofMillis(50).toNanos();
    String amperEnvelope;
    String beaconHealth;
    String mechanismReadiness;

    WorldSnapshotBuilder() {
    }

    public WorldSnapshotBuilder snapshotId(String snapshotId) {
        this.snapshotId = snapshotId;
        return this;
    }

    public WorldSnapshotBuilder timestampNanos(long timestampNanos) {
        this.timestampNanos = timestampNanos;
        return this;
    }

    public WorldSnapshotBuilder pose(PoseEstimate pose) {
        this.pose = pose;
        return this;
    }

    public WorldSnapshotBuilder addTarget(ObservedTarget target) {
        this.targets.add(Objects.requireNonNull(target, "target"));
        return this;
    }

    public WorldSnapshotBuilder targets(List<ObservedTarget> targets) {
        this.targets = new ArrayList<>(Objects.requireNonNull(targets, "targets"));
        return this;
    }

    public WorldSnapshotBuilder heldGamePiece(HeldGamePiece heldGamePiece) {
        this.heldGamePiece = heldGamePiece;
        return this;
    }

    public WorldSnapshotBuilder capability(CapabilityState state) {
        Objects.requireNonNull(state, "state");
        this.capabilities.put(state.capability(), state);
        return this;
    }

    public WorldSnapshotBuilder capability(Capability capability, CapabilityAvailability availability, String provider) {
        return capability(CapabilityState.builder(capability)
                .availability(availability)
                .provider(provider)
                .timestampNanos(timestampNanos)
                .usableByCurrentTask(availability.mayBeUsed(true))
                .build());
    }

    public WorldSnapshotBuilder resource(Resource resource, boolean available) {
        this.resourcesAvailable.put(Objects.requireNonNull(resource, "resource"), available);
        return this;
    }

    public WorldSnapshotBuilder condition(ConditionResult result) {
        Objects.requireNonNull(result, "result");
        this.conditions.put(result.name(), result);
        return this;
    }

    public WorldSnapshotBuilder confidence(ConfidenceDimension dimension, Confidence confidence) {
        this.confidences.put(Objects.requireNonNull(dimension, "dimension"),
                Objects.requireNonNull(confidence, "confidence"));
        return this;
    }

    public WorldSnapshotBuilder safetyRestriction(SafetyRestriction restriction) {
        this.safetyRestrictions.add(Objects.requireNonNull(restriction, "restriction"));
        return this;
    }

    public WorldSnapshotBuilder remainingAutonomous(RemainingTime remainingAutonomous) {
        this.remainingAutonomous = remainingAutonomous;
        return this;
    }

    public WorldSnapshotBuilder remainingMatch(RemainingTime remainingMatch) {
        this.remainingMatch = remainingMatch;
        return this;
    }

    public WorldSnapshotBuilder currentTaskName(String currentTaskName) {
        this.currentTaskName = currentTaskName;
        return this;
    }

    public WorldSnapshotBuilder operatorStrategy(String operatorStrategy) {
        this.operatorStrategy = operatorStrategy;
        return this;
    }

    public WorldSnapshotBuilder operatorDisable(boolean operatorDisable) {
        this.operatorDisable = operatorDisable;
        return this;
    }

    public WorldSnapshotBuilder timestampAlignment(TimestampAlignment timestampAlignment) {
        this.timestampAlignment = timestampAlignment;
        return this;
    }

    public WorldSnapshotBuilder alignmentWindow(Duration window) {
        this.alignmentWindowNanos = Objects.requireNonNull(window, "window").toNanos();
        return this;
    }

    public WorldSnapshotBuilder amperEnvelope(String amperEnvelope) {
        this.amperEnvelope = amperEnvelope;
        return this;
    }

    public WorldSnapshotBuilder beaconHealth(String beaconHealth) {
        this.beaconHealth = beaconHealth;
        return this;
    }

    public WorldSnapshotBuilder mechanismReadiness(String mechanismReadiness) {
        this.mechanismReadiness = mechanismReadiness;
        return this;
    }

    public WorldSnapshot build() {
        return new WorldSnapshot(this);
    }
}
