# Mentor guide

HELM is a teaching architecture, not a magic auto.

## How to introduce it

1. Keep the team's conventional autonomous working with HELM off.
2. Ask students to name goals, tasks, timeouts, and fallbacks on paper.
3. Encode those names with `Goal`, `Task`, and `IntentTree` (Phase 0).
4. Record what the existing auto did with `Helm.observe` (Phase 1).
5. Run `Helm.validate` on the paper plan and read the errors together (Phase 2).
6. Do **not** skip to execute modes.

## What to refuse

- “Just make it pick the best spike.” That is Phase 4–6 and needs TRACE data.
- Enabling execute flags to see what happens. Physical output is refused, but the habit is unsafe.
- Treating unknown vision as “no game piece.”
- Copying season point values into core.

## Review questions

- What happens if pose confidence is unknown?
- Who can override HELM?
- How do we turn HELM off without rewriting Pedro or MIMIC?
- Which phase are we in, and what is explicitly out of scope?
