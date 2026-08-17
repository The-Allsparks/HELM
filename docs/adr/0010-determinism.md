# ADR 0010 — Determinism

## Context

AdvantageKit documents unordered maps, RNG, NT inputs, and raw timestamps as replay killers.

## Decision

`HelmClock` is injected. Snapshot ids are timestamp-based by default, not UUID. Linked maps for ordered iteration. No file/network on evaluate. Schema and HELM versions are constants.

## Alternatives considered

`System.currentTimeMillis()` everywhere; UUID snapshot ids; HashMap iteration for candidates.

## Consequences

Tests can replay eligibility. Production must still feed the same clock domain into snapshots.

## Student impact

“Same inputs → same explanation” is a lab exercise.

## Safety impact

Replay cannot depend on thread races for a go/no-go.

## Revisit conditions

If randomness is ever allowed, it must be seeded and TRACE-recorded.
