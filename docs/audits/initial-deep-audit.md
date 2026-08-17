# HELM initial deep audit

| Field | Value |
|-------|--------|
| **Date of audit** | 2026-08-17 |
| **Audited commit SHA** | `cb214b2e080ccc34d5536b5c0434a0c7cb143bd5` |
| **Audited branch** | `feature/phase-0-passive-scaffold` |
| **Default branch** | `main` (`d9801ec` public stub: `LICENSE` + `README.md` only) |
| **Auditor identity** | `TA-C-GHill` (GitHub; working identity `TA-C-GHILL`) |
| **Repository** | [The-Allsparks/HELM](https://github.com/The-Allsparks/HELM) |
| **Automatic merge** | **false** (not authorized) |

This audit inspects the Phase 0–2 scaffold on the feature branch, not the empty default-branch stub. Findings cite code, tests, documentation, or GitHub state. Speculative defects without evidence are omitted.

Severity uses the orchestrator definitions: `BLOCKER` prevents safe continued development; `CRITICAL` could cause unsafe robot behavior, corrupt fundamental results, or invalidate the architecture; `HIGH` materially affects reliability, integration, or major goals; `MEDIUM` is important but does not block the next safe vertical slice; `LOW` is local cleanup; `INFORMATIONAL` requires no current action.

---

## Executive summary

HELM is an experimental, passive-first coordinator. The scaffold on `feature/phase-0-passive-scaffold` matches the stated purpose: vocabulary, observation, and offline validation with **no hardware output**. `AuthorityGate.allowsPhysicalOutput()` is hard-coded `false`. There is no FTC SDK, motor, or servo usage in `src/`. Desktop CI on the draft PR is green (36 tests).

The library is **not** on `main`. Draft PR [#16](https://github.com/The-Allsparks/HELM/pull/16) is the current delivery vehicle. Phase 3+ issues [#9](https://github.com/The-Allsparks/HELM/issues/9)–[#15](https://github.com/The-Allsparks/HELM/issues/15) are correctly blocked. Readiness gates in `docs/readiness-gates.md` are honest.

The highest-value defects are in **Phase 2 validation honesty**, not in robot control:

1. `PlanValidator` infers “safe terminal” from action-name substrings (`park` / `safe` / `hold`), including false positives such as `UnsafeDrive`, contradicting ADR 0012 and `IntentNodeKind` (“does not interpret season-specific action names”).
2. Tree node and depth limits ignore named subtrees, so oversized or conflicting trees can pass static checks.
3. When validation is skipped (mode `OFF` or Phase 2 flag off), `ValidationReport.isValid()` is `true` and the explanation says the plan is valid.

None of these can energize hardware in this scaffold. They **can** teach students that an unsafe or unvalidated plan is “valid.” That is the next work, not Phase 3 execution.

**Readiness:** Continue Phase 0–2 desktop hardening. Do not enable execute modes. Do not merge Dependabot major upgrades without a compatibility review. Do not treat PR [#16](https://github.com/The-Allsparks/HELM/pull/16) as complete Phase 2 until the validator defects are fixed or explicitly deferred.

---

## Project purpose

HELM is the high-level execution and logic manager for Allsparks FTC robots: validate goals, describe intent trees, observe stated intent, and later (only after gates) select or execute tasks through adapters. It must not own motors, pathing, vision, mechanism interlocks, electrical protection, or season scoring.

**Intended users:** beginning students (describe/observe/validate), advanced students (trees and eligibility), mentors (gates and teaching), integrating teams (one feature at a time), downstream maintainers (no compile deps on siblings).

**Maturity:** `0.1.0-SNAPSHOT`, experimental, student-learning project. Hardware validation: **none**.

**Implementation phase:** Phase 0 vocabulary, Phase 1 passive observation, Phase 2 offline validation, plus desktop-only `SimulatedTreeWalker`. Phases 3–9 are designed, flagged off, and refused by the authority gate.

---

## Current maturity

| Claim | Evidence | Status |
|-------|----------|--------|
| Experimental, not competition-ready | README “Read this first”; ADR 0015 | Accurate |
| Default mode `OFF` | `HelmConfig.Builder.mode = HelmMode.OFF` | Accurate |
| No hardware command | No SDK imports; `NoOpActionAdapter`; `allowsPhysicalOutput()` always false | Accurate |
| Phase 3/5 not approved | `HelmFeatureFlags` defaults; issues #9/#11 `blocked` | Accurate |
| TRACE sibling empty | `NoOpTraceSink`; readiness table | Accurate as of audit date |
| Desktop tests exist | 8 test classes; PR #16 reports 36 tests | Accurate |
| Phase 2 “safe terminal required” | `PlanValidator.hasSafeTerminal` name heuristic | **Overstated** — see F-ARCH-001, F-SAFE-001 |
| Library available on default branch | `main` contains only LICENSE + README | **Not met** until PR #16 (plus follow-up) merges |

---

## Implemented capabilities

- Immutable `WorldSnapshot` with unknown/stale conditions, capabilities, confidence, timestamp alignment, operator disable, and opaque lower-layer strings (`amperEnvelope`, `beaconHealth`, `mechanismReadiness`).
- `Task` / `Goal` eligibility via `TaskEvaluator` (timeout required, unknown/stale/degraded handling, safety restrictions, remaining-time unknown ≠ infinite).
- Four-valued `ConditionValue` (`TRUE`/`FALSE`/`UNKNOWN`/`STALE`); missing snapshot facts are UNKNOWN.
- `Helm.observe` bounded history (capacity 64) when OBSERVE + Phase 1 flag.
- `PlanValidator` for tasks and trees (timeouts, fallbacks, retries, cycles, parallel exclusive resources, empty control nodes, size limits on the root walk only).
- `AuthorityGate` refuse-all physical output; replay and execute modes still cannot command hardware.
- `ManualClock` / `HelmClock`; `RecordingTraceSink` / `NoOpTraceSink`.
- Desktop `SimulatedTreeWalker` for sequence/fallback/parallel/timeout/retry/guard/cancel/preempt.
- Feature flags, modes, schema version `0.1.0`.
- Research (`docs/research/*`), ADRs 0001–0015, student/mentor docs, CI (`check` on Ubuntu + Windows), Dependabot.

## Documented but unimplemented capabilities

| Capability | Where documented | Implementation |
|------------|------------------|----------------|
| Phase 3 static tree execution | README, ADR 0015, issue #9 | Refused; no live executor |
| Phase 4 shadow scoring | `Helm.recommend`, issue #10 | Always `DecisionRecord.disabled` |
| Phase 5 bounded substitution | issue #11 | Flag exists; no selector |
| Phases 6–9 | issues #12–#15 | Flags only; #8 replay is eligibility re-eval, not TRACE replay |
| Pedro/MIMIC/ViDAR/AMPER/BEACON/TRACE adapters | `docs/architecture.md` Layer 4 | Not implemented (correct) |
| Season strategy module | `docs/season-strategy.md`, ADR 0012 | Out of core (correct) |
| OpMode lifecycle / DS disable wiring | `docs/safety.md` operator disable | Config boolean only |
| Compilable examples | `examples/README.md` | Sketches + unit tests only |
| Control Hub performance proof | `docs/performance.md` | Explicitly unmeasured |

Advanced behavior is **not** implemented before prerequisites; flags and modes exist as vocabulary. That is appropriate.

---

## Architecture findings

### F-ARCH-001 — Safe-terminal check interprets action names

- **Severity:** HIGH
- **Type:** ARCHITECTURE / SAFETY
- **Evidence:** `PlanValidator.hasSafeTerminal` lowercases `node.name()` and treats `contains("park")`, `contains("safe")`, or `contains("hold")` as a safe terminal. `IntentNodeKind` javadoc: “does not interpret season-specific action names.” ADR 0012 forbids season vocabulary in core.
- **Impact:** `"UnsafeDrive"` contains `"safe"` and would pass. `"HoldPiece"` contains `"hold"` and would pass. `"ScorePreload"` with no park fails, which is intended, but the pass path is a naming convention, not a structural invariant.
- **Also:** Recursion accepts a matching action **anywhere** in a sequence, not only as a fallback/recovery last child.

### F-ARCH-002 — Tree limits and resource checks do not count named subtrees

- **Severity:** HIGH
- **Type:** ARCHITECTURE / CORRECTNESS
- **Evidence:** `IntentTree.nodeCount()` / `depth()` walk `children` only. `IntentNode.subtree(name)` has no children. `PlanValidator.validate(IntentTree)` applies `maxTreeNodes` / `maxTreeDepth` to those undercounts, then `walk()` follows `tree.subtrees()`. `detectResourceConflicts` does not enter named subtree maps.
- **Impact:** A one-node root that references a huge or conflicting subtree can pass size and parallel-resource checks.

### F-ARCH-003 — Simulated walker does not use `IntentTree.subtrees()`

- **Severity:** MEDIUM
- **Type:** ARCHITECTURE
- **Evidence:** `SimulatedTreeWalker.tick` calls `tickNode(tree.root())`. `SUBTREE`/`DECORATOR` tick the first child or `UNAVAILABLE`. Named subtree tables used by the validator are ignored.
- **Impact:** A tree that validates via subtree references cannot be walked equivalently in desktop simulation.

### F-ARCH-004 — `ResourceClaim` is unused

- **Severity:** LOW
- **Type:** ARCHITECTURE
- **Evidence:** Grep shows the type exists only in its own file. Claims are not part of snapshot or validator.

### F-ARCH-005 — `Condition` evaluators are lambdas

- **Severity:** MEDIUM
- **Type:** ARCHITECTURE / COMPATIBILITY
- **Evidence:** `Condition` stores `Function<WorldSnapshot, ConditionResult>`. Fine for Phase 0–2 in-process tests; not a serializable tree definition for Phase 8 replay of task preconditions.
- **Defer:** Until TRACE schema work (issue #14). Do not invent a DSL now.

### F-ARCH-006 — Facade vs walker authority

- **Severity:** LOW
- **Type:** ARCHITECTURE / SAFETY
- **Evidence:** `Helm` never constructs `SimulatedTreeWalker`. `AuthorityGate.allowsSimulatedTick()` is unused. Walker has no mode check.
- **Impact:** Students could tick trees in a test without going through `Helm`. Acceptable for desktop-only simulation if docs stay clear (`docs/architecture.md` already says it is not robot execution).

God objects, hidden globals, circular compile deps, and hardware in core were **not** found. Package boundaries match `docs/architecture.md`. No compile dependency on ViDAR, Pedro, MIMIC, AMPER, BEACON, TRACE, NextFTC, FTCLib, or Dairy.

---

## Correctness findings

### F-CORR-001 — Skipped validation reports `isValid() == true`

- **Severity:** HIGH
- **Type:** CORRECTNESS / USABILITY
- **Evidence:** `Helm.validate` returns `new ValidationReport(name, List.of())` when mode is `OFF` or Phase 2 is disabled. `ValidationReport.isValid()` is “no ERROR findings.” `explanation()` then returns `Plan '…' is valid`. `docs/troubleshooting.md` already documents this footgun.
- **Impact:** Callers that only check `isValid()` cannot tell “not run” from “passed.”

### F-CORR-002 — Future timestamps are treated as fresh

- **Severity:** MEDIUM
- **Type:** CORRECTNESS
- **Evidence:** `WorldSnapshot.isFresh` is `nowNanos - timestampNanos <= maxAgeNanos`. If `timestampNanos > nowNanos`, the difference is negative and the snapshot is fresh.
- **Impact:** Clock skew or mis-set snapshot time can bypass stale detection. No test covers future timestamps.

### F-CORR-003 — Remaining-time check treats one unknown clock as non-blocking

- **Severity:** MEDIUM
- **Type:** CORRECTNESS
- **Evidence:** `TaskEvaluator.evaluateTime` rejects only when **both** remaining auto and remaining match are unknown. If auto is known and sufficient, unknown match time still passes (`matchOk = !isKnown() || hasAtLeast`).
- **Impact:** TeleOp-oriented minimums can pass without match time. May be intended; it is an untested invariant.

### F-CORR-004 — Retry reset does not clear nested node state

- **Severity:** LOW
- **Type:** CORRECTNESS
- **Evidence:** `tickRetry` does `states.remove(child)` only for the direct child. Nested descendants keep `NodeState`.
- **Impact:** Desktop retry of composite children can skip re-entry. No test uses a composite retry child.

### F-CORR-005 — `Helm.observe` allows a null snapshot

- **Severity:** LOW
- **Type:** CORRECTNESS
- **Evidence:** `evaluate`/`validate` require non-null; `observe` uses `snapshot == null ? "" : snapshot.snapshotId()`. Test `observeIsIgnoredWhenOff` passes `null`.
- **Impact:** Inconsistent API; NPE avoided. Prefer requiring a snapshot or an explicit empty id.

Task eligibility unknown/stale/degraded paths have tests in `HelmEligibilityTest` and behave as documented. Deterministic re-evaluation of the same snapshot is tested. Tie-break tests cover `CommitmentPolicy` only (Phase 4 unused).

---

## Safety findings

### F-SAFE-001 — Validator can certify structurally unsafe trees

- **Severity:** HIGH
- **Type:** SAFETY
- **Evidence:** Same as F-ARCH-001. Phase 2’s job is to reject unsafe plans **before** anyone later executes them. A false pass is a safety defect of the validator even though Phase 3 is off.
- **Not CRITICAL:** No path issues hardware commands (`allowsPhysicalOutput()` false; no SDK).

### F-SAFE-002 — Physical output is refused in every shipped mode

- **Severity:** INFORMATIONAL (positive)
- **Type:** SAFETY
- **Evidence:** `AuthorityGate.allowsPhysicalOutput()` returns `false` unconditionally. Tests: `defaultHelmRefusesPhysicalOutput`, `replayModeNeverAllowsPhysicalOutput` (even with Phase 3 + Phase 8 flags). `NoOpActionAdapter.tick` returns `UNAVAILABLE`.

### F-SAFE-003 — Unknown safety is not treated as available

- **Severity:** INFORMATIONAL (positive)
- **Type:** SAFETY
- **Evidence:** `Task.allowUnknownSafetyAsAvailable` is hard-coded `false`. Missing capabilities become `CapabilityState.unknown` and reject. `CapabilityAvailability.mayBeUsed` is false for UNKNOWN/STALE/UNAVAILABLE.

### F-SAFE-004 — Operator disable is a config/snapshot bit, not Driver Station wiring

- **Severity:** MEDIUM
- **Type:** SAFETY / INTEGRATION
- **Evidence:** `HelmConfig.operatorDisable` default `false`; snapshot `operatorDisable` evaluated in `TaskEvaluator`. No OpMode `stop()` or gamepad binding in this library (correct for a pure-Java core).
- **Impact:** Robot application must wire the bit. Documented as application-owned; still a later integration issue, not a Phase 0–2 blocker.

### F-SAFE-005 — Replay cannot produce physical outputs

- **Severity:** INFORMATIONAL (positive)
- **Type:** SAFETY
- **Evidence:** Replay mode denial string; `allowsPhysicalOutput()` false; walker is not an adapter to motors.

Passive modes do not command hardware. Failures do not emit stale motor commands because HELM never emits motor commands.

---

## Performance findings

All performance claims in `docs/performance.md` are **budgets**, not Control Hub measurements. Desktop tests use a jumping clock for the 5 ms decision budget; they are not device timings.

| Item | Observation | Class |
|------|-------------|--------|
| Decision budget 5 ms | Enforced after full evaluation; marks incomplete | INFORMATIONAL (design) |
| History capacity 64 | `ArrayList.remove(0)` on overflow — O(n), 64 max | LOW / PERFORMANCE |
| `evaluateAll` allocates a new list | Caller-sized | INFORMATIONAL |
| No file/network on decision path | Confirmed in core | INFORMATIONAL (positive) |
| `TraceEvent` uses `Map.of` / `LinkedHashMap` per record | Allocation on observe/evaluate | INFORMATIONAL until measured |
| Control Hub CPU/GC/USB | **Not measured** | RESEARCH — do not optimize yet |

**F-PERF-001** (MEDIUM / PERFORMANCE / RESEARCH): No benchmark issue existed at audit time. Create a desktop characterization issue (allocation/decision nanos under `ManualClock`) before any “make it faster” work. Hardware profiling remains blocked on a Control Hub.

---

## API / usability findings

### F-USE-001 — README quick start omits completion condition

- **Severity:** MEDIUM
- **Type:** USABILITY / DOCUMENTATION
- **Evidence:** README student API builds a `Task` with capabilities, timeout, and fallback only. `PlanValidator.validate(Task)` errors on missing completion. `examples/README.md` includes `.completion(...)`. Tests use completion.
- **Impact:** First-copy students get a task that evaluates but fails validation.

### F-USE-002 — No compilable example module

- **Severity:** MEDIUM
- **Type:** USABILITY
- **Evidence:** `examples/` contains only `README.md`. CONTRIBUTING tells students to run `gradlew test`.

### F-USE-003 — `recommend` always disabled

- **Severity:** INFORMATIONAL
- **Type:** USABILITY
- **Evidence:** Even with SHADOW + Phase 4 flag, returns “not implemented.” Correct for this phase; error text is clear.

Progressive adoption is good: default `OFF`, flags off, no classpath auto-enable. Naming is generally student-readable. `Helm.create()` is safe.

---

## Testing findings

| Area | Coverage | Gap |
|------|----------|-----|
| Eligibility / unknown / stale / degraded | `HelmEligibilityTest` | Strong |
| Observe on/off | `ObserveAndValidateTest` | Strong |
| Task validation missing fields | `ObserveAndValidateTest` | Strong |
| Tree sequence/fallback/parallel/timeout/retry/guard | `IntentTreeBehaviorTest` | Good |
| Safe terminal false positives (`Unsafe*`) | **None** | F-TEST-001 HIGH |
| Skipped validation ≠ valid | **None** (troubleshooting only) | F-TEST-002 HIGH |
| Subtree size-limit bypass | **None** | F-TEST-003 HIGH |
| Snapshot immutability | `WorldSnapshotImmutabilityTest` | Good |
| Decision budget | `DeterminismAndReplayTest` | Good (fake clock) |
| Schema | `SchemaCompatibilityTest` | Version strings only |
| Doc files exist | `DocLinkCheckerTest` | Does not check markdown hrefs |
| Android / FTC SDK compile | **None** | F-TEST-004 MEDIUM |
| Concurrency / cancellation of `Helm` facade | **None** | INFORMATIONAL (single-threaded by design) |
| Hardware | **None** | Documented |

No disabled tests found. Tests run in CI `./gradlew check`. Tests do not require hardware. `studentApiExampleCompilesAndEvaluates` asserts `isEligible() || !explanation().isBlank()`, which is a weak assertion.

---

## Documentation findings

Documentation is unusually complete for a 0.1 scaffold and generally matches implementation. Defects:

| ID | Severity | Finding |
|----|----------|---------|
| F-DOC-001 | MEDIUM | README quick start vs validator completion (same as F-USE-001) |
| F-DOC-002 | LOW | `docs/intent-trees.md` says validator rejects missing safe terminals without defining how a terminal is recognized |
| F-DOC-003 | INFORMATIONAL | Troubleshooting honestly describes skipped validation as “valid” — should change when F-CORR-001 is fixed |
| F-DOC-004 | LOW | CI `docs-structure` and `DocLinkCheckerTest` omit `docs/audits/` (will be stale until updated with this file) |
| F-DOC-005 | INFORMATIONAL | Research docs distinguish fact/inference/hypothesis; keep that standard |

No claim of Control Hub validation was found in README current-status (correct).

---

## Dependency findings

| Item | State | Class |
|------|-------|--------|
| Runtime deps | **None** beyond JDK | INFORMATIONAL (positive) |
| Test | JUnit BOM 5.10.2 | INFORMATIONAL |
| Gradle wrapper | 8.7 (`gradle-8.7-bin.zip`) | Matches AMPER convention |
| Java | source/target 11; CI Temurin 17 | Matches convention |
| Javadoc | `failOnError = false` | LOW |
| Actions | `actions/checkout@v4`, `actions/setup-java@v4` (mutable tags, not SHAs) | MEDIUM / SECURITY |
| Dependabot | Open PRs #1 JUnit 6.1.3, #2 Gradle 9.7.0, #3 setup-java v5, #4 checkout v7 | HIGH / COMPATIBILITY if merged blindly |
| NextFTC | Correctly excluded (GPL-3.0) | INFORMATIONAL (positive) |
| Secrets in repo | None found in workflows | INFORMATIONAL (positive) |

**F-DEP-001** (HIGH / COMPATIBILITY): Do not merge Dependabot PRs [#1](https://github.com/The-Allsparks/HELM/pull/1)–[#4](https://github.com/The-Allsparks/HELM/pull/4) without a dedicated compatibility issue. Gradle 9 and JUnit 6 are major upgrades relative to the documented Java 11 + Gradle 8.7 Allsparks standard.

**F-DEP-002** (MEDIUM / SECURITY): Pin GitHub Actions to full SHAs after a reviewed minor/patch bump, consistent with supply-chain hygiene. Do not jump major Action versions in the same change as the scaffold.

---

## Repository-health findings

| Item | State | Class |
|------|-------|--------|
| Default branch | `main`, unprotected, no rulesets | HIGH / SECURITY (F-REPO-001) |
| Merge methods | merge, squash, rebase all allowed; delete-on-merge false | INFORMATIONAL |
| Required checks | None | HIGH (same as F-REPO-001) |
| CODEOWNERS | Missing | MEDIUM |
| Viewer permission | ADMIN (`TA-C-GHill`) | INFORMATIONAL |
| Open implementation PR | Draft [#16](https://github.com/The-Allsparks/HELM/pull/16), CI success | HIGH process — current delivery |
| Issues | #5 roadmap; #6–#8 Phase 0–2 (acceptance checked in body, still open); #9–#15 later phases blocked | INFORMATIONAL |
| Labels | Phase 0–9, safety, testing, experimental, blocked, hardware, student-ready | Missing severity/type labels until this audit’s backlog work |
| Milestones | Phase 0–9, one issue each | INFORMATIONAL |
| Releases | None | INFORMATIONAL |
| Changelog | Present for 0.1.0-SNAPSHOT | INFORMATIONAL |
| Dependabot | Configured monthly | INFORMATIONAL |
| GitHub Projects | Token missing `read:project`; ledger lives in-repo | INFORMATIONAL |
| Stale Dependabot branches | Four open upgrade PRs | MEDIUM (F-DEP-001) |
| Issue/PR templates | Present | INFORMATIONAL (positive) |
| Security policy | Present; no private maintainer email published | LOW |

**F-REPO-001** (HIGH / SECURITY): Anyone with write access can merge to `main` without review or CI. Enabling branch protection is a human/org settings change, not a library code change.

**F-REPO-002** (MEDIUM / DOCUMENTATION): Roadmap issue #5 still has unchecked “each phase has an independently reviewable issue” even though #6–#15 exist. Update after backlog linking.

---

## Cross-project integration findings

Conceptual direction `ViDAR / Pedro / AMPER / MIMIC / BEACON → TRACE → HELM` is documented as **snapshot-in, adapter-out**, not compile-time edges. HELM core honors that.

| Sibling | HELM contract | Status |
|---------|---------------|--------|
| TRACE | `TraceSink` / `TraceEvent` | No-op; issue #14 blocked on TRACE existing |
| BEACON | `CapabilityState`, `SafetyRestriction`, `beaconHealth` string | BEACON stub; no adapter |
| AMPER | `amperEnvelope` string | Not consumed semantically |
| MIMIC | `mechanismReadiness` string; no adapter | MIMIC Phase 0 elsewhere |
| ViDAR | `ObservedTarget`, conditions | No live adapter |
| Pedro | Pose on snapshot; future `PedroActionAdapter` | Not wrapped |
| Robot app | Must build snapshots and wire operator disable | No sample OpMode (intentional) |

No circular compile dependency. Opaque strings are honest placeholders; do not parse them into fake envelopes.

**F-INT-001** (INFORMATIONAL / INTEGRATION): Cross-repo contracts should remain local issues until siblings expose typed APIs. Do not add Gradle dependencies to those repos.

---

## Readiness assessment

| Gate | Result |
|------|--------|
| Safe to keep developing Phase 0–2 on desktop | **Yes** |
| Safe to merge Dependabot majors | **No** |
| Safe to enable Phase 3/5 | **No** — readiness-gates.md unmet |
| Safe to run on a competition robot | **No** |
| Phase 2 acceptance as written in issue #8 | **Not fully met** — safe-terminal and limit checks are weaker than claimed |
| Automatic merge of PR #16 | **Not authorized** |

HELM must remain `OFF` / `OBSERVE` / `VALIDATE`. Creating or merging this repository is not authorization to enable HELM on a robot.

---

## Recommended work order

1. **Validator honesty (Phase 2)** — explicit safe-terminal marking; subtree-aware limits and resource checks; skipped validation is not `isValid()`.
2. **Land the passive scaffold on `main`** after (1) or with (1) included — human merge of the current PR stream; `AUTOMATIC_MERGE=false`.
3. **Repository gates** — branch protection, required `CI` checks, CODEOWNERS (human settings + small policy PR).
4. **Dependabot** — close or rewrite majors; consider SHA-pinning Actions on a reviewed minor line.
5. **Docs/examples** — README quick start, `docs/intent-trees.md` terminal definition, optional compilable examples.
6. **Simulation fidelity** — walker resolves named subtrees (after validator limits).
7. **Freshness / remaining-time invariants** — future timestamps; document or tighten match-time rules.
8. **Measurement** — desktop characterization; Control Hub only when hardware is available.
9. **Phase 4 shadow** — only after Phase 0–2 claims are true and tests lock them.
10. **Phase 3+** — remain blocked on readiness gates.

---

## Deferred or rejected ideas

| Idea | Decision | Reason |
|------|----------|--------|
| Enable EXECUTE_STATIC samples | Rejected | ADR 0015; readiness unmet |
| Adopt NextFTC / FTCLib / Dairy / BehaviorTree.CPP as core | Rejected | ADR 0001; GPL / scope |
| Season point tables in core | Rejected | ADR 0012 |
| Broad Gradle 9 / JUnit 6 / Actions major bump with scaffold | Deferred | F-DEP-001 |
| Control Hub performance rewrite | Deferred | Unmeasured |
| Typed AMPER/BEACON envelope objects | Deferred | Siblings not ready; keep opaque strings |
| Serialize `Condition` lambdas | Deferred | Phase 8 / TRACE |
| GitHub Project board | Deferred | Token lacks `read:project`; use `docs/priority-ledger.md` |
| Second implementation PR to `main` while #16 is open | Rejected by orchestrator policy | One implementation stream |

---

## Evidence and references

- Repository: https://github.com/The-Allsparks/HELM
- Audited commit: `cb214b2e080ccc34d5536b5c0434a0c7cb143bd5`
- Draft PR: https://github.com/The-Allsparks/HELM/pull/16
- Roadmap: https://github.com/The-Allsparks/HELM/issues/5
- Key source: `src/main/java/org/allsparks/helm/validate/PlanValidator.java`, `Helm.java`, `authority/AuthorityGate.java`, `intent/IntentTree.java`, `sim/SimulatedTreeWalker.java`, `snapshot/WorldSnapshot.java`, `task/TaskEvaluator.java`
- Key docs: `README.md`, `docs/readiness-gates.md`, `docs/architecture.md`, `docs/safety.md`, `docs/adr/0012-season-strategy-separation.md`, `docs/adr/0015-authority-gates.md`
- CI: workflow `CI` run `32062975646` success on PR #16 (Ubuntu + Windows `check`, docs-structure)

### Finding index

| ID | Severity | Type | Ready for issue |
|----|----------|------|-----------------|
| F-ARCH-001 / F-SAFE-001 | HIGH | SAFETY / ARCHITECTURE | Yes — first slice |
| F-ARCH-002 | HIGH | ARCHITECTURE / CORRECTNESS | Yes — after or split from first |
| F-CORR-001 | HIGH | CORRECTNESS / USABILITY | Yes |
| F-TEST-001/002/003 | HIGH | TESTING | Fold into the three issues above |
| F-DEP-001 | HIGH | COMPATIBILITY | Yes — do-not-merge analysis |
| F-REPO-001 | HIGH | SECURITY | Yes — blocked on human settings |
| F-ARCH-003 | MEDIUM | ARCHITECTURE | Yes — after F-ARCH-002 |
| F-CORR-002 | MEDIUM | CORRECTNESS | Yes |
| F-CORR-003 | MEDIUM | CORRECTNESS | Yes — needs invariant decision |
| F-USE-001 / F-DOC-001 | MEDIUM | DOCUMENTATION | Yes |
| F-USE-002 | MEDIUM | USABILITY | Yes |
| F-SAFE-004 | MEDIUM | INTEGRATION | Blocked on robot app |
| F-DEP-002 | MEDIUM | SECURITY | Yes — after F-DEP-001 policy |
| F-TEST-004 | MEDIUM | TESTING / COMPATIBILITY | Yes — or explicitly defer |
| F-ARCH-005 | MEDIUM | ARCHITECTURE | Blocked on Phase 8 |
| F-PERF-001 | MEDIUM | RESEARCH | Yes — desktop only |
| F-ARCH-004 / F-CORR-004 / F-CORR-005 | LOW | various | Optional |
| F-SAFE-002/003/005 | INFORMATIONAL | SAFETY | No issue |

---

## Finding classification recap

No `BLOCKER` (development can continue on desktop). No `CRITICAL` (no hardware command path, no silent coordinate-frame mutation, no replay-to-motors path). Highest actionable class is `HIGH` Phase 2 validator honesty plus repository merge hygiene.
