# Performance

HELM must fit FTC Android / Control Hub constraints.

| Budget | Default in this scaffold | Behavior on exceed |
|--------|--------------------------|--------------------|
| Decision frequency | Once per snapshot / caller tick | Caller-controlled; HELM does not spin |
| Decision-time budget | 5 ms (`HelmConfig.decisionTimeBudget`) | Mark evaluation **incomplete**; do not return a partial selection as complete |
| Snapshot max age | 100 ms | `STALE_INPUT` |
| Max tree depth | 16 | Validation error after reachable subtree expansion |
| Max tree nodes | 64 | Validation error after reachable subtree expansion |
| Max candidates | 8 | Recommendation refused |
| History capacity | 64 events | Drop oldest |

Rules:

- `maxTreeNodes` / `maxTreeDepth` apply after reachable named-subtree expansion. Unreachable named subtrees are warnings and are not counted toward those limits.
- No blocking in the OpMode loop
- No file I/O or network on the decision path
- No evaluating every possible future task continuously (selection not enabled)
- Minimal allocation: snapshots are built by the application; HELM copies on evaluate
- Prevalidated trees; preloaded configuration

Worst-case evaluation cost is linear in required capabilities, confidence dimensions, preconditions, and resource checks for a single task. Desktop unit tests measure completeness under a jumping clock; they are **not** Control Hub timings.

**Hardware performance measurements: none.** Do not claim Control Hub budgets are proven.
