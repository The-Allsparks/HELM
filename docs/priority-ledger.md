# HELM priority ledger

Maintained by the repository orchestrator. Source: [initial deep audit](audits/initial-deep-audit.md). **`main`:** `fdd8920` after [#32](https://github.com/The-Allsparks/HELM/pull/32).

**Identity:** TA-C-GHill. **Max active subagents:** 1.

## Current stream

| Field | Value |
|-------|--------|
| Selected issue | [#20](https://github.com/The-Allsparks/HELM/issues/20) walker named subtrees |
| Branch | `fix/issue-20-walker-named-subtrees` |
| Status | Implementation |

## Ledger (abbreviated)

| Issue | Status | PR |
|-------|--------|-----|
| #17 | Closed | [#27](https://github.com/The-Allsparks/HELM/pull/27) merged |
| #18 | Closed | [#31](https://github.com/The-Allsparks/HELM/pull/31) merged |
| #19 | Closed | [#32](https://github.com/The-Allsparks/HELM/pull/32) merged |
| #20 | In progress | pending |
| #6 #7 | Closed | via #27 |
| #8 epic | Open | #17–#19 done; #20 closes desktop parity |
| #21 | Blocked | human branch protection |
| #22 | Ready | do not merge Dependabot #4, #28–#30 |
| #9–#15 | Blocked | readiness gates |

## Next after #20

1. [#23](https://github.com/The-Allsparks/HELM/issues/23) README completion (small docs)
2. [#24](https://github.com/The-Allsparks/HELM/issues/24) future-dated snapshots
3. [#22](https://github.com/The-Allsparks/HELM/issues/22) Dependabot analysis (process, not a library change)

## Required human decisions

- Enable branch protection (#21)
- Dependabot major-upgrade policy (#22)
- FTC SDK compile job (audit F-TEST-004; not filed)
