# Capabilities

HELM consumes semantic capabilities. It does not diagnose hardware faults.

Well-known names (not a closed season set): `DRIVE_TRANSLATION`, `DRIVE_ROTATION`, `PRECISE_LOCALIZATION`, `COARSE_LOCALIZATION`, `VISION_TARGETING`, `GAME_PIECE_ACQUISITION`, `LOW_SCORING`, `HIGH_SCORING`, `CLIMBING`, `FULL_PERFORMANCE`, `REDUCED_ACCELERATION`, `POWER_BURST`.

`CapabilityAvailability`: `AVAILABLE`, `DEGRADED`, `UNAVAILABLE`, `UNKNOWN`, `STALE`.

Well-known constants stay in HELM. Students name a custom capability with `Capability.named(...)` (and `toCapabilityId()`) without editing HELM or allsparks-contracts. Shared `Availability` has no `STALE` value: `HelmMappings` maps `AVAILABLE`/`DEGRADED`/`UNAVAILABLE`/`UNKNOWN` onto the same names, and maps `STALE` to `Validity.STALE` (freshness). HELM eligibility still refuses `UNKNOWN` and `STALE` via the local enum (`mayBeUsed` stays HELM policy).

Each `CapabilityState` identifies provider, timestamp, reason, restrictions, optional expected recovery, and whether it may be used by the current task.

Unknown or stale capability data **blocks** tasks that require that capability. Degraded is usable only if the task opts in.
