# ADR 0001 — Build versus adopt

## Context

HELM needs explainable eligibility, unknown/stale data, capabilities, and replay on FTC Android without replacing Pedro or MIMIC.

## Decision

Build a small independent Java core with optional adapters. Do not adopt NextFTC, FTCLib, Dairy, Ivy, Road Runner, BehaviorTree.CPP, or AdvantageKit as the core.

## Alternatives considered

Adopt NextFTC (GPL-3.0); extend FTCLib; wrap Ivy; import BehaviorTree.CPP; hybrid merge.

## Consequences

Core stays MIT and SDK-free. Teams already on Ivy/Pedro keep those runtimes. Adapters are extra work later.

## Student impact

Students learn HELM vocabulary without a foreign framework tax.

## Safety impact

`OFF` and no-op adapters are enforceable because HELM is not a hardware scheduler.

## Revisit conditions

A maintained MIT/BSD FTC library provides unknown/stale conditions, capability objects, and replay-safe clocks without owning motors.
