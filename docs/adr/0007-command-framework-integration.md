# ADR 0007 — Command-framework integration

## Context

Ivy, NextFTC, FTCLib, Dairy, and Road Runner Actions all run hardware-oriented commands.

## Decision

No compile dependency. Future adapters implement `ActionAdapter`. NextFTC is excluded from core because of GPL-3.0. Allsparks-first adapter, when approved, is Pedro/Ivy + MIMIC.

## Alternatives considered

Fork Ivy into HELM; require FTCLib; generate commands by reflection.

## Consequences

Examples stay plain Java. Magic annotations are forbidden.

## Student impact

Control flow stays visible.

## Safety impact

Adapters can disappear → `UNAVAILABLE`, not last-known motion.

## Revisit conditions

Team standardizes on one framework; still adapter, not merge.
