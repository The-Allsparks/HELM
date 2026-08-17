# Team code case studies

Access date: **2026-08-17**. These are public competition-team or maintainer codebases, not Allsparks robots.

## Team 254 — FRC 2025 Undertow

**Source:** [Team254/FRC-2025-Public](https://github.com/Team254/FRC-2025-Public) (MIT).

**Documented facts from the README:**

- Superstructure state machine coordinates multi-step actions across elevator, wrist, claw, intake, indexer, and climber.
- Modal controls switch operator/robot behavior among CORAL, ALGAECLIMB, and CORALMANUAL.
- `CoralStateTracker` follows a game piece through intake → indexer → claw → staged.
- Subsystems use AdvantageKit IO interfaces (hardware vs simulation).
- Automatic scoring height (L1–L4) is based on reef branch availability — **season-specific**.

**Transfer to HELM:**

- Keep a timestamped robot/world snapshot rather than querying hardware from the planner.
- Treat possession as its own confidence dimension, not a boolean that defaults to empty.
- Put superstructure sequencing in MIMIC (or Ivy commands), not in HELM core.
- Keep L1–L4 style scoring tables in a season module.

**Do not transfer:** WPILib types, AdvantageKit, or 2025 Reefscape scoring policy.

## Team 254 — FRC 2024 Vortex

**Source:** [Team254/FRC-2024-Public](https://github.com/Team254/FRC-2024-Public).

**Documented facts:** controller modes (SPEAKER, HP, CLIMB, and others) change which commands are legal; `RobotState` tracks frames of reference; simulation tracks note state.

**Transfer:** explicit modes with an operator-visible current mode; degraded/manual modes when automation is untrusted (`NOT_SPECIFIED` / manual recovery in 254's writeup). HELM modes (`OFF` … `REPLAY`) are the analogous control, not a copy of 2024 shooter modes.

## Mechanical Advantage — AdvantageKit users

**Source:** [AdvantageKit](https://docs.advantagekit.org/getting-started/what-is-advantagekit/) and [non-deterministic data sources](https://docs.advantagekit.org/getting-started/common-issues/non-deterministic-data-sources).

**Transfer:** log inputs, not a few outputs; replay in simulation; ban unordered iteration and hidden wall-clock reads from the decision path.

**Do not transfer:** `LoggedRobot`, WPILOG, or NetworkTables loggers into HELM. TRACE is the Allsparks recording plane.

## Pedro Pathing + Ivy example OpMode

**Source:** [Ivy OpModes](https://pedropathing.com/docs/ivy/creating-opmodes).

**Documented facts:** `Scheduler.reset()` at OpMode start; `Scheduler.execute()` every loop; `sequential(raiseArm, waitMs(200), openClaw)` composition; cancel APIs.

**Transfer:** HELM leaf adapters must be cancellable and must not block the loop. Reset/disable must be possible without rewriting subsystems.

## Road Runner Actions autonomous style

**Source:** [Road Runner Actions](https://rr.brott.dev/docs/v1-0/actions/).

**Documented facts:** sequential + parallel action trees; custom `Action.run` must return quickly; stop button interruption is reliable only if actions do not `Thread.sleep`.

**Transfer:** HELM evaluation and any future executor tick must be non-blocking. Timeouts belong in HELM policy, not in hidden sleeps.

## What we did not find

We did not find a public FTC library that:

- represents UNKNOWN/STALE conditions as first-class values
- consumes semantic capability health from a communications layer
- records explainable task rejection with replay
- keeps season scoring policy out of the reusable core

**Inference:** HELM fills that gap as a thin layer, not as a replacement auto framework.
