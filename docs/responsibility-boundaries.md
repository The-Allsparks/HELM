# Responsibility boundaries

## HELM owns

High-level goals, candidate tasks, preconditions, success requirements, failure outcomes, task eligibility, capability **requirements**, resource **requirements**, intent-tree **structure**, bounded retries, timeouts, fallback **selection policy**, task utility evaluation (future), objective preemption (future), decision explanation, decision history, shadow recommendations (future), replayable high-level decisions.

## HELM does not own

Motor or servo control, low-level motion, path following, pose estimation, sensor fusion, computer vision, mechanism calibration, mechanism interlocks, electrical protection, communication-loss detection, emergency stopping, hardware safety, raw telemetry storage, dashboard visualization, field-specific perception, season-specific scoring policy.

## Overrides

Safety decisions from MIMIC, AMPER, BEACON, the FTC SDK, or the robot application always override HELM. HELM may choose among permitted safe actions. It may not weaken or bypass a lower-layer restriction.

## Dependency direction

```text
Robot application
    → HELM core (no reverse compile deps)
        → no-op adapters
Lower layers feed snapshots into the application, not into HELM internals.
```

Circular dependencies are prohibited. HELM core must not depend on any one Allsparks project, command framework, pathing library, dashboard, or season strategy.
