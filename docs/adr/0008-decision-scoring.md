# ADR 0008 — Decision scoring

## Context

Utility-based selection is easy to overfit and chatter.

## Decision

Do **not** ship live scoring. Provide `CommitmentPolicy` (hysteresis, commitment window, name tie-break) as a pure function. Season weights stay outside core. Phase 4 shadow must be explicitly flagged later.

## Alternatives considered

Hard-coded utility in core; machine learning; always pick first eligible.

## Consequences

`recommend()` explains that Phase 4 is disabled. No silent objective switching.

## Student impact

Students compare manual strategy to a future policy without robot risk.

## Safety impact

A slightly higher score cannot steal the current task in this version because selection is off.

## Revisit conditions

After TRACE has real autos and students can explain the conventional routine.
