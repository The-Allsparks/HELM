# Changelog

All notable changes to HELM will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.0-SNAPSHOT] - 2026-08-17

### Added

- Compilable Phase 0 desktop example at `src/test/java/org/allsparks/helm/examples/Phase0DescribeExampleTest.java` (CI `check`; not an OpMode).
- Desktop performance characterization: `ManualClock` records `evaluationNanos == 0`; the 5 ms budget is a clock-delta policy, not a Control Hub measurement.
- Phase 0 vocabulary: goals, tasks, conditions, capabilities, resources, outcomes, and intent-tree structure.
- Phase 1 passive observation of stated intent and outcomes.
- Phase 2 offline/static plan validation.
- Desktop-only simulated intent-tree walker for tests.
- No-op TRACE and action adapters.
- Authority gate that refuses physical output in every mode.
- Research, architecture, ADR, and student documentation.

### Safety

- Default mode is `OFF`.
- HELM does not command motors or servos.
- Unknown and stale inputs are not converted to booleans.
- Replay cannot create physical outputs.
- Intent-tree safe terminals are an explicit `safeTerminal` mark in last-child fallback/recovery position. Name substrings such as `park` / `safe` / `hold` are not treated as safety proofs. Trees that relied on `ParkSafely` by name must call `IntentNode.safeTerminal(...)` (SNAPSHOT break; no release yet).
- `PlanValidator` applies `maxTreeNodes` / `maxTreeDepth` and parallel exclusive-resource checks to reachable expanded subtrees. Unreachable named subtrees remain warnings and are not counted toward live limits (SNAPSHOT stricter validation).
- Skipped validation reports `NOT_RUN` status; `ValidationReport.isValid()` is false unless validation actually ran and found no errors. Empty skipped reports are no longer treated as valid (SNAPSHOT behavioral break).
- `SimulatedTreeWalker` resolves named `IntentTree.subtrees()` with a cyclic-reference guard. Missing or cyclic subtrees are `UNAVAILABLE`, never success. Desktop simulation only; still no hardware adapters.
- Future-dated world snapshots (`timestampNanos` after `HelmClock`) are not fresh and fail eligibility with `STALE_INPUT`. There is no clock-skew budget (SNAPSHOT stricter validation).

### Changed

- README Phase 0 student snippet includes a completion condition so it matches `PlanValidator`.
- Dependabot ignores Gradle wrapper and JUnit BOM **major** upgrades so they stay on the Allsparks Gradle 8.7 / JUnit 5 convention.
- CI pins `actions/checkout` v4.4.0 and `actions/setup-java` v4.9.1 by full commit SHA. Action major bumps are ignored.
- JUnit BOM 5.14.4 with an explicit `junit-platform-launcher` on the test runtime classpath so Gradle 8.7 does not use its bundled launcher (5.12+ otherwise fails every test).
