# Decision explanations

Every evaluation produces student-readable text plus structured `FailureReason` codes.

A complete selection explanation (Phase 4+, not implemented as authority) must include: selected task, eligible candidates, rejected candidates, rejection reasons, score components, thresholds, required vs available capabilities, confidence requirements, predicted duration, remaining-time margin, and fallback.

This scaffold records:

- `TaskEvaluation.explanation()` and `rejectionReasons()`
- `ValidationReport.explanation()`
- TRACE events for evaluation, observation, and validation
- `DecisionRecord.explanation()` for refused recommendations

HELM must never conceal uncertainty or imply that a decision is intelligent merely because it was automated.
