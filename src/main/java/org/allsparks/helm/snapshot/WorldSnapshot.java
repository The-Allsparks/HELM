package org.allsparks.helm.snapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.allsparks.helm.capability.Capability;
import org.allsparks.helm.capability.CapabilityAvailability;
import org.allsparks.helm.capability.CapabilityState;
import org.allsparks.helm.condition.ConditionResult;
import org.allsparks.helm.confidence.Confidence;
import org.allsparks.helm.confidence.ConfidenceDimension;
import org.allsparks.helm.resource.Resource;

/**
 * Immutable, timestamped world view for one decision cycle. HELM never queries
 * hardware; callers assemble this snapshot from lower-layer reports.
 */
public final class WorldSnapshot {
    private final String snapshotId;
    private final long timestampNanos;
    private final Optional<PoseEstimate> pose;
    private final List<ObservedTarget> targets;
    private final HeldGamePiece heldGamePiece;
    private final Map<Capability, CapabilityState> capabilities;
    private final Map<Resource, Boolean> resourcesAvailable;
    private final Map<String, ConditionResult> conditions;
    private final Map<ConfidenceDimension, Confidence> confidences;
    private final List<SafetyRestriction> safetyRestrictions;
    private final RemainingTime remainingAutonomous;
    private final RemainingTime remainingMatch;
    private final Optional<String> currentTaskName;
    private final Optional<String> operatorStrategy;
    private final boolean operatorDisable;
    private final TimestampAlignment timestampAlignment;
    private final Optional<String> amperEnvelope;
    private final Optional<String> beaconHealth;
    private final Optional<String> mechanismReadiness;

    WorldSnapshot(WorldSnapshotBuilder builder) {
        this.snapshotId = builder.snapshotId == null || builder.snapshotId.isBlank()
                ? "snapshot-" + builder.timestampNanos
                : builder.snapshotId;
        this.timestampNanos = builder.timestampNanos;
        this.pose = Optional.ofNullable(builder.pose);
        this.targets = Collections.unmodifiableList(List.copyOf(builder.targets));
        this.heldGamePiece = builder.heldGamePiece == null
                ? HeldGamePiece.unknown(builder.timestampNanos)
                : builder.heldGamePiece;
        this.capabilities = Collections.unmodifiableMap(new LinkedHashMap<>(builder.capabilities));
        this.resourcesAvailable = Collections.unmodifiableMap(new LinkedHashMap<>(builder.resourcesAvailable));
        this.conditions = Collections.unmodifiableMap(new LinkedHashMap<>(builder.conditions));
        this.confidences = Collections.unmodifiableMap(new LinkedHashMap<>(builder.confidences));
        this.safetyRestrictions = Collections.unmodifiableList(List.copyOf(builder.safetyRestrictions));
        this.remainingAutonomous = builder.remainingAutonomous == null
                ? RemainingTime.unknown() : builder.remainingAutonomous;
        this.remainingMatch = builder.remainingMatch == null
                ? RemainingTime.unknown() : builder.remainingMatch;
        this.currentTaskName = Optional.ofNullable(builder.currentTaskName).filter(s -> !s.isBlank());
        this.operatorStrategy = Optional.ofNullable(builder.operatorStrategy).filter(s -> !s.isBlank());
        this.operatorDisable = builder.operatorDisable;
        this.timestampAlignment = builder.timestampAlignment == null
                ? computeAlignment(builder)
                : builder.timestampAlignment;
        this.amperEnvelope = Optional.ofNullable(builder.amperEnvelope).filter(s -> !s.isBlank());
        this.beaconHealth = Optional.ofNullable(builder.beaconHealth).filter(s -> !s.isBlank());
        this.mechanismReadiness = Optional.ofNullable(builder.mechanismReadiness).filter(s -> !s.isBlank());
    }

    public static WorldSnapshotBuilder builder() {
        return new WorldSnapshotBuilder();
    }

    public String snapshotId() {
        return snapshotId;
    }

    public long timestampNanos() {
        return timestampNanos;
    }

    public Optional<PoseEstimate> pose() {
        return pose;
    }

    public List<ObservedTarget> targets() {
        return targets;
    }

    public HeldGamePiece heldGamePiece() {
        return heldGamePiece;
    }

    public Map<Capability, CapabilityState> capabilities() {
        return capabilities;
    }

    public Optional<CapabilityState> capability(Capability capability) {
        return Optional.ofNullable(capabilities.get(capability));
    }

    public CapabilityState capabilityOrUnknown(Capability capability) {
        return capabilities.getOrDefault(
                capability,
                CapabilityState.unknown(capability, "world-snapshot", timestampNanos));
    }

    public Map<Resource, Boolean> resourcesAvailable() {
        return resourcesAvailable;
    }

    public boolean resourceAvailable(Resource resource) {
        Boolean available = resourcesAvailable.get(resource);
        return Boolean.TRUE.equals(available);
    }

    public boolean resourceKnown(Resource resource) {
        return resourcesAvailable.containsKey(resource);
    }

    public Map<String, ConditionResult> conditions() {
        return conditions;
    }

    public Optional<ConditionResult> condition(String name) {
        return Optional.ofNullable(conditions.get(name));
    }

    public Map<ConfidenceDimension, Confidence> confidences() {
        return confidences;
    }

    public Confidence confidence(ConfidenceDimension dimension) {
        Confidence sample = confidences.get(dimension);
        if (sample != null) {
            return sample;
        }
        if (dimension.equals(ConfidenceDimension.POSITION)) {
            return pose.map(PoseEstimate::positionConfidence).orElse(Confidence.unknown());
        }
        if (dimension.equals(ConfidenceDimension.HEADING)) {
            return pose.map(PoseEstimate::headingConfidence).orElse(Confidence.unknown());
        }
        if (dimension.equals(ConfidenceDimension.POSSESSION)) {
            return heldGamePiece.possessionConfidence();
        }
        return Confidence.unknown();
    }

    public List<SafetyRestriction> safetyRestrictions() {
        return safetyRestrictions;
    }

    public boolean hasBlockingSafetyRestriction() {
        for (SafetyRestriction restriction : safetyRestrictions) {
            if (restriction.blocksAllMotion()) {
                return true;
            }
        }
        return false;
    }

    public RemainingTime remainingAutonomous() {
        return remainingAutonomous;
    }

    public RemainingTime remainingMatch() {
        return remainingMatch;
    }

    public Optional<String> currentTaskName() {
        return currentTaskName;
    }

    public Optional<String> operatorStrategy() {
        return operatorStrategy;
    }

    public boolean operatorDisable() {
        return operatorDisable;
    }

    public TimestampAlignment timestampAlignment() {
        return timestampAlignment;
    }

    public Optional<String> amperEnvelope() {
        return amperEnvelope;
    }

    public Optional<String> beaconHealth() {
        return beaconHealth;
    }

    public Optional<String> mechanismReadiness() {
        return mechanismReadiness;
    }

    public boolean isFresh(long nowNanos, long maxAgeNanos) {
        return nowNanos - timestampNanos <= maxAgeNanos;
    }

    private static TimestampAlignment computeAlignment(WorldSnapshotBuilder builder) {
        List<Long> stamps = new ArrayList<>();
        stamps.add(builder.timestampNanos);
        if (builder.pose != null) {
            stamps.add(builder.pose.timestampNanos());
        }
        for (ObservedTarget target : builder.targets) {
            stamps.add(target.timestampNanos());
        }
        stamps.add(builder.heldGamePiece == null
                ? builder.timestampNanos
                : builder.heldGamePiece.timestampNanos());
        for (CapabilityState state : builder.capabilities.values()) {
            stamps.add(state.timestampNanos());
        }
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        for (long stamp : stamps) {
            min = Math.min(min, stamp);
            max = Math.max(max, stamp);
        }
        long spread = max - min;
        if (spread > builder.alignmentWindowNanos) {
            return TimestampAlignment.misaligned(spread,
                    "Source timestamps differ by " + spread + " ns, exceeding the "
                            + builder.alignmentWindowNanos + " ns alignment window");
        }
        return TimestampAlignment.aligned(spread);
    }

    public WorldSnapshotBuilder toBuilder() {
        WorldSnapshotBuilder copy = new WorldSnapshotBuilder();
        copy.snapshotId = snapshotId;
        copy.timestampNanos = timestampNanos;
        copy.pose = pose.orElse(null);
        copy.targets = new ArrayList<>(targets);
        copy.heldGamePiece = heldGamePiece;
        copy.capabilities = new LinkedHashMap<>(capabilities);
        copy.resourcesAvailable = new LinkedHashMap<>(resourcesAvailable);
        copy.conditions = new LinkedHashMap<>(conditions);
        copy.confidences = new LinkedHashMap<>(confidences);
        copy.safetyRestrictions = new ArrayList<>(safetyRestrictions);
        copy.remainingAutonomous = remainingAutonomous;
        copy.remainingMatch = remainingMatch;
        copy.currentTaskName = currentTaskName.orElse(null);
        copy.operatorStrategy = operatorStrategy.orElse(null);
        copy.operatorDisable = operatorDisable;
        copy.timestampAlignment = timestampAlignment;
        copy.amperEnvelope = amperEnvelope.orElse(null);
        copy.beaconHealth = beaconHealth.orElse(null);
        copy.mechanismReadiness = mechanismReadiness.orElse(null);
        return copy;
    }
}
