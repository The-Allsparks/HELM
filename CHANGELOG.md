# Changelog

All notable changes to HELM will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.0-SNAPSHOT] - 2026-08-17

### Added

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
