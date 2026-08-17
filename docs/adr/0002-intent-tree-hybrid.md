# ADR 0002 — Behavior tree vs state machine vs hybrid

## Context

FRC teams often use superstructure state machines (Team 254). Nav2 and BehaviorTree.CPP use trees with fallbacks and recoveries. Command frameworks use groups.

## Decision

**Hybrid:** inspectable intent **trees** for composition (sequence/fallback/parallel/timeout/retry) plus explicit `HelmMode` and later task selection **outside** the tree. Mechanism state machines stay in MIMIC.

## Alternatives considered

Pure enum state machine for autos; full BehaviorTree.CPP port; command groups only.

## Consequences

Trees are data, validatable, and student-readable. We do not tick a C++/XML engine. Phase 3 robot execution still requires approval.

## Student impact

Sequence and fallback map to how students already think about “try A else park.”

## Safety impact

Invalid trees can be rejected before any executor exists.

## Revisit conditions

If trees become unteachable, fall back to named static sequences only.
