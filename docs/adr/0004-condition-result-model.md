# ADR 0004 — Condition-result model

## Context

Booleans hide missing vision and stale pose.

## Decision

Four values: `TRUE`, `FALSE`, `UNKNOWN`, `STALE`. Unknown/stale never coerce to true or false. Preconditions that need certainty reject both.

## Alternatives considered

Optional boolean; tri-state without stale; confidence-only.

## Consequences

Callers must populate conditions. Tests assert the distinction.

## Student impact

“We didn’t see it” is not “it isn’t there.”

## Safety impact

Prevents scoring or intaking on missing evidence.

## Revisit conditions

None without a new evidence type that is still non-boolean.
