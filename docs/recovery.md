# Recovery

Distinguish four levels:

1. **Action recovery** — local retry or small correction. Bounded attempts and duration required.
2. **Task recovery** — change approach or target (for example, abandon a lost piece).
3. **Plan recovery** — change the autonomous objective (for example, stop cycling and park).
4. **System safety** — cancel the plan because BEACON/MIMIC/SDK requires a safe state. HELM cannot override this.

Unbounded retries are prohibited. `RetryPolicy` requires positive max attempts and max duration. Exhaustion must have a fallback. A new authorized goal or safety restriction must be able to preempt recovery; simulated walker exposes `preempt()` as `PREEMPTED`, distinct from `FAILED` and `CANCELLED`.
