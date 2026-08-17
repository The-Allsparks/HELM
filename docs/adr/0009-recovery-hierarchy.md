# ADR 0009 — Recovery hierarchy

## Context

Nav2 warns that a tree without recoveries is incomplete. Unbounded retries damage mechanisms.

## Decision

Four levels: action, task, plan, system safety. Retries require max attempts, max duration, and fallback after exhaustion. Safety preemption is a distinct `PREEMPTED` status.

## Alternatives considered

Single retry loop; infinite retry until timeout; HELM overrides BEACON.

## Consequences

Validators require fallbacks and timeouts. Safety always wins.

## Student impact

“Try again” has a stopping rule students can state.

## Safety impact

No infinite intake jams from HELM policy.

## Revisit conditions

If MIMIC defines richer recovery contracts, map them to task/plan levels without collapsing statuses.
