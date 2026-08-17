# Architecture

HELM is a four-layer coordinator. It never queries hardware and never owns motor output.

```text
Lower-layer snapshots
        ↓
HELM world snapshot          (Layer 1)
        ↓
Goal and task evaluation     (Layer 2)
        ↓
Intent-tree structure        (Layer 3 — describe/validate now; execute later)
        ↓
Capability adapters          (Layer 4 — no-op only in this scaffold)
        ↓
Existing subsystem actions
```

Core package: `org.allsparks.helm`. No compile dependency on ViDAR, Pedro, MIMIC, AMPER, BEACON, TRACE, Ivy, NextFTC, FTCLib, or Dairy.

## Layer 1 — World snapshot

One immutable, timestamped `WorldSnapshot` per decision cycle. See [world-snapshot.md](world-snapshot.md).

## Layer 2 — Goal and task evaluation

`Goal` names a desired outcome. `Task` names an action that may advance it, with capabilities, resources, confidence needs, timeout, retry, and fallback. `Helm.evaluate` returns an explainable `TaskEvaluation`. Dynamic scoring is **not** implemented.

## Layer 3 — Intent trees

`IntentTree` / `IntentNode` store inspectable composition (sequence, fallback, parallel, timeout, retry, recovery, …). Phase 2 validates trees. `SimulatedTreeWalker` ticks trees against in-memory leaves for desktop tests. It is not robot execution.

## Layer 4 — Adapters

`ActionAdapter` is the leaf contract. This scaffold ships `NoOpActionAdapter` only. Justified future adapters (not implemented):

- `PedroActionAdapter` — Allsparks chassis
- `MimicActionAdapter` — mechanisms
- `VidarConditionAdapter` — observations as conditions
- `AmperCapabilityAdapter` — performance envelope
- `BeaconCapabilityAdapter` — health / restrictions
- `TraceDecisionAdapter` — validated recording

Road Runner, NextFTC, FTCLib, and Dairy adapters are **not** implemented; they are optional if a downstream team already uses those runtimes. NextFTC cannot be a core dependency (GPL-3.0).

## Modes and authority

`HelmMode` defaults to `OFF`. `AuthorityGate.allowsPhysicalOutput()` is **always false** in this version. Execute modes may be set for tests of denial messaging; they still cannot command hardware.

## Package map

| Package | Role |
|---------|------|
| `org.allsparks.helm` | Facade, config, mode, flags |
| `clock` | Deterministic time |
| `snapshot` | Immutable world view |
| `goal` / `task` | Vocabulary and eligibility |
| `condition` / `confidence` | Four-valued conditions |
| `capability` / `resource` | Semantic availability and logical exclusion |
| `intent` | Tree structure and statuses |
| `validate` | Offline plan checks |
| `observe` | Phase 1 history |
| `decision` | Records and commitment policy |
| `trace` | Sink interface |
| `adapter` | No-op leaf contract |
| `authority` | Output refusal |
| `sim` | Desktop walker |
| `outcome` | Distinct results |

## Intentional deviations from AMPER/MIMIC conventions

Preserved: MIT, Java 11, Gradle 8.7, JUnit 5, `org.allsparks.*`, Temurin 17 CI, LF, editorconfig, Contributor Covenant, phase flags, passive-first README.

Deviations:

- Documentation lives at `docs/*.md` plus `docs/research/` and `docs/adr/` rather than a single domain folder. HELM spans many concerns.
- `HelmMode` exists in addition to phases because observation, validation, shadow, execute, and replay are operator-visible states, not only build flags.
- No REV hardware observers. HELM must not talk to hubs.
- Snapshot identifiers default to `snapshot-{timestamp}` rather than UUID so tests stay deterministic.

## Control flow (eventual, not enabled)

ViDAR → pose → BEACON → AMPER → HELM → Pedro → MIMIC → TRACE.

Safety decisions from MIMIC, AMPER, BEACON, the FTC SDK, or the robot application always override HELM.
