# Goals and tasks

A **goal** is a named desired outcome (`Goal.named("ScorePreload")`).

A **task** is a named action that may advance a goal. Tasks declare:

- required capabilities
- whether degraded capabilities are acceptable
- required logical resources
- per-dimension confidence requirements
- preconditions, completion conditions, failure conditions
- timeout (required for Phase 2 validity)
- bounded retry policy
- fallback task name (required for Phase 2 validity)
- expected duration and minimum remaining time (optional)

Season point values, field targets, and scoring tables stay out of HELM core. See [season-strategy.md](season-strategy.md).

`Helm.evaluate(task, snapshot)` is eligibility, not dynamic selection. It explains every rejection. A slightly higher future utility score must not chatter; see `CommitmentPolicy` (used when Phase 4 exists).
