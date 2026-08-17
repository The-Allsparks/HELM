# Student learning path

Progression:

```text
Describe → Observe → Validate → Explain → Execute → Recover → Select → Predict
```

| Phase | Objective | This scaffold |
|-------|-----------|----------------|
| 0 Describe | Students can describe what the robot intends and how success or failure is recognized | **Implemented** |
| 1 Observe | Students can reconstruct what the auto attempted and what happened | **Implemented** (in-memory + TRACE sink) |
| 2 Validate | Students can explain why a plan is or is not safe to run | **Implemented** (offline) |
| 3 Execute static trees | Compose behavior from small actions | Simulated desktop only; **robot execution blocked** |
| 4 Shadow | Compare a selection policy with a manual strategy without risk | API refuses; not implemented |
| 5 Bounded substitution | Delegate one constrained decision | Blocked pending approval |
| 6 Cycles | Respond to task outcomes, not assumed success | Deferred |
| 7 Degraded | Distinguish graceful degradation from unsafe continuation | Eligibility handles unknown/stale/degraded; live degraded auto deferred |
| 8 Replay / faults | Prove a planning change without weakening safety | Desktop determinism tests only |
| 9 Predict | Use recorded evidence to improve predictions | Deferred; no ML |

Each phase is optional, feature-flagged, TRACE-observable, reversible, and has a disable path. Students must understand the current phase before enabling the next.
