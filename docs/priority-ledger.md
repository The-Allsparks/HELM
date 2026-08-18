# HELM priority ledger

Maintained by the repository orchestrator. Source: [initial deep audit](audits/initial-deep-audit.md). **`main`:** `5093fd2` after [#35](https://github.com/The-Allsparks/HELM/pull/35).

**Identity:** TA-C-GHill. **Max active subagents:** 1.

## Current stream

| Field | Value |
|-------|--------|
| Selected issue | [#22](https://github.com/The-Allsparks/HELM/issues/22) Dependabot policy |
| Branch | `fix/issue-22-dependabot-policy` |
| Status | Implementation |

## Ledger (abbreviated)

| Issue | Status |
|-------|--------|
| #17–#20, #24, #6–#8 | Closed |
| #23 | Closed via [#35](https://github.com/The-Allsparks/HELM/pull/35) |
| #21 | Blocked — branch protection |
| #22 | In progress — close Gradle 9 / JUnit 6; wait on Actions majors |
| #25 | Ready — desktop characterization, no Control Hub claims |
| #26 | Ready — CI-compiled example path |
| #9–#15 | Blocked — readiness gates |

## Required human decisions

- Enable branch protection (#21)
- SHA-pin GitHub Actions v4 line (follow-up from #22; PRs #4 and #28 wait)
- FTC SDK compile job (audit F-TEST-004; not filed)
