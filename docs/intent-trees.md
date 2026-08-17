# Intent trees

Intent trees are inspectable composition, not a C++ BehaviorTree.CPP port.

Supported node kinds: sequence, fallback/selector, parallel, condition, action, decorator, timeout, retry, guard, subtree, recovery, wait, succeed, fail.

Every node has a deterministic `IntentStatus`:

`READY`, `RUNNING`, `SUCCEEDED`, `FAILED`, `BLOCKED`, `CANCELLED`, `TIMED_OUT`, `PREEMPTED`, `UNAVAILABLE`.

These statuses are not collapsed into one generic failure.

## This scaffold

- Trees can be built with the student API (`IntentTree.named(...).sequence(...)`).
- `PlanValidator` rejects missing timeouts, unbounded/missing retry policy, missing safe terminals, cyclic subtrees, exclusive resource conflicts in parallel, empty control nodes, and oversize trees.
- `SimulatedTreeWalker` ticks trees against in-memory leaves for unit tests.
- **No** Pedro/MIMIC adapter is invoked. **No** execute mode can produce physical output.

Phase 3 static execution on a robot requires a later approval gate.
