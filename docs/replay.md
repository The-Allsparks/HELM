# Replay

Replay re-evaluates recorded decisions without physical outputs.

Required inputs: world snapshot, strategy configuration, task registry, clock, previous execution state, explicit seed if randomness is ever allowed, HELM version, schema version (`0.1.0`).

Avoid: unrecorded wall-clock reads, hardware access, unordered collections affecting selection, unrecorded dashboard values, unrecorded randomness, thread timing, hidden static mutable state.

Equal scores use a documented name-order tie-break (`CommitmentPolicy.tieBreak`).

`HelmMode.REPLAY` cannot create physical outputs. `AuthorityGate.allowsPhysicalOutput()` is false in every mode of this scaffold.

Replay comparison (Phase 8, partial): original vs recalculated eligibility and explanations. Live policy scoring is not implemented, so replay currently re-runs `evaluate` on stored snapshots in unit tests.
