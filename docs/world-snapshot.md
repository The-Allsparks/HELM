# World snapshot

HELM receives one immutable `WorldSnapshot` per decision cycle. It does not query hardware.

## Required properties

- Snapshot id (deterministic if omitted: `snapshot-{timestampNanos}`)
- Decision timestamp (nanoseconds on `HelmClock`)
- Optional pose with its own timestamp and position/heading confidence
- Observed targets with separate classification and position confidence
- Held-game-piece state that may be **unknown**, not silently empty
- Capability states, resource availability, named conditions
- Remaining autonomous and match time (unknown is not infinite)
- Safety restrictions
- Operator disable and optional operator strategy
- Timestamp alignment report

## Alignment

Unrelated timestamps are not combined without checking age and spread. If source timestamps differ by more than the alignment window (default 50 ms), `TimestampAlignment.aligned()` is false and tasks that require a coherent world are ineligible.

## Freshness

`HelmConfig.snapshotMaxAge` (default 100 ms) compares snapshot time to `HelmClock`. Stale snapshots fail eligibility with `STALE_INPUT`.

## Mutation

`WorldSnapshotBuilder` copies collections on `build()`. Returned lists and maps are unmodifiable.
