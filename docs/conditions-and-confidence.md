# Conditions and confidence

Boolean conditions are insufficient.

`ConditionValue`: `TRUE`, `FALSE`, `UNKNOWN`, `STALE`.

A `ConditionResult` reports result, source, timestamp, age, confidence, explanation, evidence ids, and optional threshold/actual values.

HELM does **not** convert:

- unknown → false or true
- stale → valid
- degraded → available
- low confidence → absent

Tasks declare **named** confidence dimensions (`position`, `heading`, `target-classification`, `target-position`, `possession`, `mechanism-state`, `time-estimate`, `capability-health`). There is no single global robot-confidence number. Unknown samples fail a numeric threshold rather than counting as 0.
