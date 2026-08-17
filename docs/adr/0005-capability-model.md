# ADR 0005 — Capability model

## Context

BEACON and AMPER should report semantic health, not raw CAN errors, to HELM.

## Decision

Named `Capability` + `CapabilityAvailability` including UNKNOWN and STALE. HELM does not diagnose faults. Degraded requires task opt-in.

## Alternatives considered

One robot health enum; HELM parsing REV errors; boolean “vision ok.”

## Consequences

Depends on lower layers actually publishing capabilities. Until BEACON exists, snapshots must fill states or tasks block.

## Student impact

Maps to “can we still drive / score low / localize coarsely?”

## Safety impact

Unknown capability is not available.

## Revisit conditions

When BEACON’s capability schema ships, align names without adding hardware diagnosis to HELM.
