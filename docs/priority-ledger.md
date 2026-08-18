# HELM priority ledger

Maintained by the repository orchestrator. Source: [initial deep audit](audits/initial-deep-audit.md). **`main`:** `bbcce52` after [#40](https://github.com/The-Allsparks/HELM/pull/40).

**Identity:** TA-C-GHill. **Max active subagents:** 1.

## Current stream

| Field | Value |
|-------|--------|
| Selected issue | [#41](https://github.com/The-Allsparks/HELM/issues/41) SHA-pin Actions v4 |
| Branch | `fix/issue-41-sha-pin-actions` |
| Status | Implementation |

## Ledger (abbreviated)

| Issue | Status |
|-------|--------|
| #17–#20, #22–#26, #6–#8 | Closed |
| #21 | Blocked — branch protection |
| #41 | In progress — SHA-pin checkout and setup-java v4 |
| #9–#15 | Blocked — readiness gates |
| #4, #28 | Close after #41; superseded by SHA pins |
| #37, #38 | Wait — Gradle 8.7 pin / JUnit 5.14 CI red |

## Required human decisions

- Enable branch protection (#21)
- FTC SDK compile job (audit F-TEST-004; not filed)
- Org-wide Gradle 8.x bump before merging #37
- Dedicated JUnit 5.14 restore-CI PR before merging #38
