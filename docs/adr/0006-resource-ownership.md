# ADR 0006 — Resource ownership

## Context

WPILib/Ivy already interrupt on subsystem requirements. MIMIC owns physical interlocks. A second live scheduler would fight them.

## Decision

HELM resources are logical and used for **static** conflict detection (especially parallel exclusive claims). Live acquisition is deferred with Phase 3.

## Alternatives considered

HELM as the only scheduler; ignore resources; duplicate Ivy priorities.

## Consequences

Phase 2 catches “drive twice in parallel.” Runtime still belongs to the command framework when one is used.

## Student impact

Students label drivetrain/intake/arm without writing a scheduler.

## Safety impact

Complements, does not replace, MIMIC interlocks.

## Revisit conditions

If Allsparks has no command framework, Phase 3 may add a minimal claim table — still not motor output.
