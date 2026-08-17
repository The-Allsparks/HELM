# HELM priority ledger

Maintained by the repository orchestrator. Source: [initial deep audit](audits/initial-deep-audit.md). **`main`:** `24241c4` after [#31](https://github.com/The-Allsparks/HELM/pull/31).

**Identity:** TA-C-GHill. **Max active subagents:** 1.

## Current stream

| Field | Value |
|-------|--------|
| Selected issue | [#19](https://github.com/The-Allsparks/HELM/issues/19) skipped validation honesty |
| Branch | `fix/issue-19-skipped-validation-not-valid` |
| Status | PR pending |

## Ledger (abbreviated)

| Issue | Status | PR |
|-------|--------|-----|
| #17 | Closed | [#27](https://github.com/The-Allsparks/HELM/pull/27) merged |
| #18 | Closed | [#31](https://github.com/The-Allsparks/HELM/pull/31) merged |
| #19 | In progress | pending |
| #20 | Ready (unblocked) | — |
| #6 #7 | Closed | via #27 |
| #8 epic | Open | #17–#19 children; #20 optional for desktop parity |
| #21 | Blocked | human branch protection |
| #22 | Ready | do not merge Dependabot #4, #28–#30 |
| #9–#15 | Blocked | readiness gates |

## Next after #19

1. [#20](https://github.com/The-Allsparks/HELM/issues/20) walker resolves named subtrees (desktop parity)
2. [#23](https://github.com/The-Allsparks/HELM/issues/23) README completion (small docs)
3. [#24](https://github.com/The-Allsparks/HELM/issues/24) future-dated snapshots

## Required human decisions

- Enable branch protection (#21)
- Dependabot major-upgrade policy (#22)
- FTC SDK compile job (audit F-TEST-004; not filed)
