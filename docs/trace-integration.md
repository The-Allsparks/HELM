# TRACE integration

TRACE is HELM's authoritative decision record. HELM core has **no compile dependency** on TRACE; the contracts JAR is not TRACE. `NoOpTraceSink` and `RecordingTraceSink` are HELM-local. TeamCode (when HELM is composed) supplies TRACE via `HelmToTraceAdapter` + an emitter that calls `Trace.session().event`.

## Evidence chain

```text
World snapshot
    → candidate tasks
    → eligibility evaluation
    → task scoring (future)
    → selected intent (future)
    → issued lower-layer actions (future)
    → observed outcome
    → prediction error (future)
```

Recorded now: snapshot id, decision cycle, task eligibility, explanation, stated-intent outcomes, validation results.

Not yet recorded (TRACE not ready): full score components, node transitions on a robot, prediction error.

Active HELM **authority** must require validated TRACE recording (`HelmFeatureFlags.requireTraceForAuthority`, default true). A no-op sink is never validated.
