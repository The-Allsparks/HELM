# HELM priority ledger

Maintained by the repository orchestrator. Source: [initial deep audit](audits/initial-deep-audit.md). **`main`:** `b7a455a` after [#36](https://github.com/The-Allsparks/HELM/pull/36).

**Identity:** TA-C-GHill. **Max active subagents:** 1.

## Current stream

| Field | Value |
|-------|--------|
| Selected issue | [#26](https://github.com/The-Allsparks/HELM/issues/26) CI-compiled example |
| Branch | `fix/issue-26-compilable-example` |
| Status | Implementation |

## Ledger (abbreviated)

| Issue | Status |
|-------|--------|
| #17–#20, #22–#24, #6–#8 | Closed |
| #21 | Blocked — branch protection |
| #25 | Ready — desktop characterization, no Control Hub claims |
| #26 | In progress — CI-compiled example path |
| #9–#15 | Blocked — readiness gates |

## Required human decisions

- Enable branch protection (#21)
- SHA-pin GitHub Actions v4 line (follow-up from #22; PRs #4 and #28 wait)
- FTC SDK compile job (audit F-TEST-004; not filed)
