# Intent trees

Intent trees are inspectable composition, not a C++ BehaviorTree.CPP port.

Supported node kinds: sequence, fallback/selector, parallel, condition, action, decorator, timeout, retry, guard, subtree, recovery, wait, succeed, fail.

Every node has a deterministic `IntentStatus`:

`READY`, `RUNNING`, `SUCCEEDED`, `FAILED`, `BLOCKED`, `CANCELLED`, `TIMED_OUT`, `PREEMPTED`, `UNAVAILABLE`.

These statuses are not collapsed into one generic failure.

## Safe terminal

A validated tree must have an explicit safe terminal in a **structurally terminal** position. Names are labels only. Core does not treat `park`, `safe`, or `hold` substrings as safety proofs.

Mark an ACTION with `IntentNode.safeTerminal("ParkSafely")` (or `IntentTree.safeTerminal(...)`). The flag defaults to false. It is valid only on ACTION nodes.

`PlanValidator` accepts a safe terminal when it sits in last-child fallback/recovery position:

- The root must be a `FALLBACK` or `RECOVERY` (optionally wrapped in `TIMEOUT`, `RETRY`, `GUARD`, or `DECORATOR`).
- Only the **last** child of that fallback/recovery is inspected. Nested fallback/recovery is allowed only as that last child.
- A timeout/retry/guard/decorator wrapper around the marked ACTION is allowed so a timed park can sit in terminal position.
- A sequence or parallel root is not a plan terminal, even if a nested fallback contains a marked park. If an earlier sequence child fails, the walker never reaches that nested park.
- A marked ACTION as a sequence prefix does not satisfy the rule.
- A lone marked ACTION (no fallback/recovery) is not a plan terminal.
- Every ACTION still needs its own timeout; the mark does not replace `MISSING_TIMEOUT`.

Valid sketch:

```java
IntentTree.named("SimpleAutonomous").fallback(
    IntentNode.builder("ScorePreload", IntentNodeKind.ACTION)
        .timeout(TimeoutPolicy.ofSeconds(6))
        .build(),
    IntentNode.timeout(
        "parkTimeout",
        Duration.ofSeconds(3),
        IntentNode.builder("ParkSafely", IntentNodeKind.ACTION)
            .safeTerminal(true)
            .timeout(TimeoutPolicy.ofSeconds(3))
            .build()));
```

## This scaffold

- Trees can be built with the student API (`IntentTree.named(...).fallback(...)`).
- `PlanValidator` rejects missing timeouts, unbounded/missing retry policy, missing safe terminals, cyclic subtrees, exclusive resource conflicts in parallel, empty control nodes, and oversize trees.
- `SimulatedTreeWalker` ticks trees against in-memory leaves for unit tests.
- **No** Pedro/MIMIC adapter is invoked. **No** execute mode can produce physical output.

Phase 3 static execution on a robot requires a later approval gate.
