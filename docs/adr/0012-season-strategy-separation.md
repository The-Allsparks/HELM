# ADR 0012 — Season-strategy separation

## Context

Team 254’s L1–L4 selection is powerful and season-specific. FTC games change annually.

## Decision

Core has no point values, spike marks, or field polygons. Season modules (future) supply tasks, weights, and legal regions.

## Alternatives considered

Decode/IntoTheDeep constants in core; JSON game files in core.

## Consequences

Reusable library. Season repos can depend on HELM, not the reverse.

## Student impact

Students author strategy next to the game manual, not inside the coordinator.

## Safety impact

Illegal-region policy cannot rot in core across seasons unnoticed — it simply is not there.

## Revisit conditions

Never put a single season’s scoring table in core.
