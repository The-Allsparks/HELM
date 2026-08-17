# Troubleshooting

| Symptom | Likely cause | What to do |
|---------|--------------|------------|
| `evaluate` always ineligible | Mode is `OFF` | Expected default. Use `VALIDATE` in desktop tests only |
| `UNKNOWN_CONDITION` | Fact missing from snapshot | Put the named condition in the snapshot; do not default it to false |
| `STALE_INPUT` | Snapshot older than max age, or timestamps misaligned | Check clocks and alignment window |
| `CAPABILITY_UNKNOWN` | BEACON/MIMIC did not report the capability | Leave the task blocked; do not invent AVAILABLE |
| `validate` returns not run / `isValid()` false with empty findings | Phase 2 flag off or mode OFF | Enable `HelmFeatureFlags.validate()` and set mode to `VALIDATE`; check `ValidationReport.status()` |
| Tree invalid despite a `ParkSafely` name | Name is not a safety proof | Mark `IntentNode.safeTerminal(...)` and put it last on a root fallback/recovery (typically inside `IntentNode.timeout(...)`). Each ACTION still needs its own timeout. |
| `observe` returns empty | Phase 1 off or mode OFF | Enable observe flags |
| Recommend disabled | Phase 4 not implemented | Expected |
| Robot moved | Not HELM | HELM has no hardware output; debug the OpMode / Pedro / MIMIC |
| Want dashboard widgets | Out of scope | Use FTC Dashboard / Panels / TRACE viewers separately |

Never “fix” unknown by coercing it to false so a task becomes eligible.
