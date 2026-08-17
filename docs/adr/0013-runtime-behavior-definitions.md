# ADR 0013 — Runtime behavior definitions

## Context

BehaviorTree.CPP loads XML trees. Untrusted runtime trees plus code execution is a safety and FTC-legal risk.

## Decision

Trees are Java data built by students at compile time (or from **prevalidated** static config later). No eval, no class loading from match-time strings, no arbitrary callbacks except `Condition` functions registered in code.

## Alternatives considered

JSON trees from Driver Station; Groovy scripts; annotation processors.

## Consequences

Student API stays explicit. Phase 2 validates structure before any future executor.

## Student impact

Control flow is readable in Java, not generated magic.

## Safety impact

Match-time payload cannot construct an active tree that drives the robot.

## Revisit conditions

A signed, schema-validated, allowlisted config format with no code pointers — still validated before use.
