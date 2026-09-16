# Teleop pose monitoring

Shop question 15 Sep 2026: can HELM watch pose during teleop?

**Yes, as a snapshot consumer. No, as an estimator.**

## In scope

- `WorldSnapshot.pose` already carries x/y/heading, timestamp, and confidence.
- `WorldSnapshot.poseIsFresh(now, maxAge)` is false when pose is missing or stale.
- Eligibility can refuse tasks that need a coherent pose (`STALE_INPUT`).
- TRACE graphs the same numbers if TeamCode records Pedro / Pinpoint pose. HELM does not own that graph.

## Out of scope

HELM does **not** own pose estimation, sensor fusion, Pinpoint, odometry, or Pedro. Those stay in TeamCode / Pedro. HELM never calls `setPower`. Mode stays `OFF` until FORGE enablement.

See [responsibility-boundaries.md](responsibility-boundaries.md) and [ADR 0003](adr/0003-world-snapshot-ownership.md).

## Shop use this season

1. Record pose in TRACE during Drive (TeamCode / Pedro). That is the teleop monitor.
2. If HELM is later used in shadow, copy that pose into `WorldSnapshot` and let `poseIsFresh` fail eligibility. Do not add a second pose filter inside HELM execute.

Do not competition-enable HELM because this helper exists.
