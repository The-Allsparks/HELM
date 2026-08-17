# ADR 0015 — Authority gates

## Context

HELM is the last major Allsparks layer. Lower layers are incomplete. Premature authority would look like intelligence while skipping safety.

## Decision

Two explicit future gates: **Phase 3** (static tree execution on a robot) and **Phase 5** (bounded allowlist substitution). This repository implements neither. Opening a PR is not approval. Modes do not auto-enable because an adapter is installed.

## Alternatives considered

Ship EXECUTE_STATIC samples; auto-enable when TRACE jar is present; mentor-only undocumented switch.

## Consequences

Students can still learn Phases 0–2. Mentors have a written stop sign.

## Student impact

Clear story: describe, observe, validate, then ask.

## Safety impact

Matches readiness gates: TRACE empty, BEACON stub, MIMIC unvalidated, no TRACE-recorded auto.

## Revisit conditions

All items in `docs/readiness-gates.md` pass, students can explain the conventional auto, and a written mentor/student approval is recorded.
