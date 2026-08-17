# ADR 0011 — TRACE integration

## Context

TRACE is the Allsparks recording plane but was an empty repo when HELM was scaffolded.

## Decision

Define `TraceSink` now. Ship no-op and recording sinks. Treat no-op as **not validated**. Authority requires validated recording when that flag is on.

## Alternatives considered

Depend on TRACE artifacts; log to files from HELM; skip logging until TRACE exists.

## Consequences

Tests prove events are emitted. Robot authority stays closed.

## Student impact

Students can dump `RecordingTraceSink` in unit tests.

## Safety impact

Cannot “go live” with silent decisions.

## Revisit conditions

When TRACE publishes a Java API, implement a real adapter without putting TRACE types in HELM core if that would cycle deps — prefer a small DTO.
