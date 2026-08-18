# HELM priority ledger

Maintained by the repository orchestrator. Source: [initial deep audit](audits/initial-deep-audit.md). **`main`:** `1a8d581` after [#42](https://github.com/The-Allsparks/HELM/pull/42).

**Identity:** TA-C-GHill. **Max active subagents:** 1.

## Current stream

| Field | Value |
|-------|--------|
| Selected issue | JUnit 5.14 + Gradle 8.7 launcher alignment (supersedes #38) |
| Branch | `fix/issue-junit-5-14-compat` |
| Status | Implementation |

## Ledger (abbreviated)

| Issue | Status |
|-------|--------|
| #17–#26, #41, #6–#8 | Closed |
| #21 | Blocked — branch protection |
| #9–#15 | Blocked — readiness gates |
| #37 | Wait — Gradle 8.7 pin until AMPER/MIMIC move |
| #38 | In progress — restore CI with `junit-platform-launcher` |

## Required human decisions

- Enable branch protection (#21)
- FTC SDK compile job (audit F-TEST-004; not filed)
- Org-wide Gradle 8.x bump before merging #37
