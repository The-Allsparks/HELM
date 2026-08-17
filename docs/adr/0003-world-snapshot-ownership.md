# ADR 0003 — World-snapshot ownership

## Context

AdvantageKit records all inputs so logic can replay. HELM must not query hardware. Multiple sources have different timestamps.

## Decision

The robot application builds one immutable `WorldSnapshot` per cycle. HELM owns the schema, not the sensors. Alignment and freshness are checked, not assumed.

## Alternatives considered

HELM pulls from ViDAR/Pedro/BEACON directly; mutable blackboard; per-layer queries inside evaluate.

## Consequences

No circular compile deps. Application is responsible for gathering. Misaligned timestamps fail eligibility.

## Student impact

Students see one object that answers “what did we believe at this instant?”

## Safety impact

Stale or split-brain data cannot silently look current.

## Revisit conditions

If TRACE becomes the snapshot bus, HELM may consume TRACE-decoded snapshots — still not hardware.
