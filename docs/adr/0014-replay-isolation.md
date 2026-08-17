# ADR 0014 — Replay isolation

## Context

Replay that accidentally calls adapters would move a robot on the stand.

## Decision

`allowsPhysicalOutput()` is unconditionally false in this version. Replay mode additionally documents isolation. No-op adapters return `UNAVAILABLE`. Simulated walker is in-memory only.

## Alternatives considered

Soft “if replay then skip write” inside adapters; honor execute flags in unit tests.

## Consequences

Even a mistaken `EXECUTE_STATIC` flag cannot emit hardware commands from this library.

## Student impact

Students can replay on a laptop safely.

## Safety impact

Hard isolation beats convention.

## Revisit conditions

Phase 3 approval may allow adapters **only** when mode is an execute mode **and** not replay **and** TRACE is validated **and** operator disable is false. Replay remains output-free forever.
