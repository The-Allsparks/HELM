package org.allsparks.helm.capability;

import java.util.Objects;

/**
 * Semantic capability name. Well-known names are constants; season-specific
 * capabilities should be defined outside HELM core.
 */
public final class Capability {
    public static final Capability DRIVE_TRANSLATION = named("DRIVE_TRANSLATION");
    public static final Capability DRIVE_ROTATION = named("DRIVE_ROTATION");
    public static final Capability PRECISE_LOCALIZATION = named("PRECISE_LOCALIZATION");
    public static final Capability COARSE_LOCALIZATION = named("COARSE_LOCALIZATION");
    public static final Capability VISION_TARGETING = named("VISION_TARGETING");
    public static final Capability GAME_PIECE_ACQUISITION = named("GAME_PIECE_ACQUISITION");
    public static final Capability LOW_SCORING = named("LOW_SCORING");
    public static final Capability HIGH_SCORING = named("HIGH_SCORING");
    public static final Capability CLIMBING = named("CLIMBING");
    public static final Capability FULL_PERFORMANCE = named("FULL_PERFORMANCE");
    public static final Capability REDUCED_ACCELERATION = named("REDUCED_ACCELERATION");
    public static final Capability POWER_BURST = named("POWER_BURST");

    private final String name;

    private Capability(String name) {
        this.name = name;
    }

    public static Capability named(String name) {
        Objects.requireNonNull(name, "name");
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Capability name must not be blank");
        }
        return new Capability(trimmed);
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Capability)) {
            return false;
        }
        return name.equals(((Capability) o).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
