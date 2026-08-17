# HELM priority ledger

Maintained by the repository orchestrator. Source of findings: [initial deep audit](audits/initial-deep-audit.md) (audited `cb214b2`; `main` after #27 is `f84b311`).

**Automatic merge:** human-authorized for this continue cycle after PR #27. **Max active implementation subagents:** 1. **Identity:** TA-C-GHill.

Priority model (highest first): safety blockers → correctness blockers → CI/build failures → issues that unblock many others → architectural seams → tests for upcoming work → small user-facing slices → measured performance → docs/usability → optional advanced capabilities → cosmetic cleanup.

## Current stream

| Field | Value |
|-------|--------|
| `main` | `f84b311` — [PR #27](https://github.com/The-Allsparks/HELM/pull/27) merged; #17 closed |
| Selected issue | [#18](https://github.com/The-Allsparks/HELM/issues/18) subtree-aware validator limits |
| Why selected | Highest remaining HIGH correctness finding; unblocks #20 |
| Branch | `fix/issue-18-subtree-limits` |
| Hardware | None |

## Ledger

| Issue | Priority | Readiness | Dependencies | Status | Branch | Pull request | CI | Merge | Blocker | Next action |
|-------|----------|-----------|--------------|--------|--------|--------------|----|-------|---------|-------------|
| [#17](https://github.com/The-Allsparks/HELM/issues/17) explicit safe terminal | 1 | Done | Scaffold | Closed | `fix/issue-17-explicit-safe-terminal` | [#27](https://github.com/The-Allsparks/HELM/pull/27) | success | merged | None | Done |
| [#18](https://github.com/The-Allsparks/HELM/issues/18) subtree limits | 2 | Ready | #17 | In progress | `fix/issue-18-subtree-limits` | pending | — | — | None | Open PR |
| [#19](https://github.com/The-Allsparks/HELM/issues/19) skipped validation not valid | 3 | Ready | Scaffold | Pending | — | — | — | — | One issue at a time | After #18 |
| [#21](https://github.com/The-Allsparks/HELM/issues/21) branch protection | 4 | Blocked | Human org settings | Blocked | n/a | n/a | n/a | n/a | Maintainer policy | Human enablement |
| [#22](https://github.com/The-Allsparks/HELM/issues/22) Dependabot majors | 5 | Ready (analysis) | None | Do not merge #4, #28–#30 | dependabot/* | #4, #28–#30 | — | forbidden until analysis | Compatibility | Analysis comments |
| [#23](https://github.com/The-Allsparks/HELM/issues/23) README completion | 6 | Ready | None | Pending | — | — | — | — | None | After #19 |
| [#20](https://github.com/The-Allsparks/HELM/issues/20) walker subtrees | 7 | Blocked | #18 | Blocked until #18 merges | — | — | — | — | #18 | Wait |
| [#24](https://github.com/The-Allsparks/HELM/issues/24) future timestamps | 8 | Ready | None | Pending | — | — | — | — | None | Later |
| [#26](https://github.com/The-Allsparks/HELM/issues/26) examples path | 9 | Ready | Scaffold | Pending | — | — | — | — | None | Later |
| [#25](https://github.com/The-Allsparks/HELM/issues/25) desktop characterization | 10 | Ready (desktop) | None | Pending | — | — | — | — | Do not claim Control Hub | Later |
| [#9](https://github.com/The-Allsparks/HELM/issues/9)–[#15](https://github.com/The-Allsparks/HELM/issues/15) Phase 3+ | 13+ | Blocked | Readiness gates | Blocked | — | — | — | — | `docs/readiness-gates.md` | Do not implement |
| [#6](https://github.com/The-Allsparks/HELM/issues/6) [#7](https://github.com/The-Allsparks/HELM/issues/7) | Delivery | Done | None | Closed via #27 | — | #27 | success | merged | None | Done |
| [#8](https://github.com/The-Allsparks/HELM/issues/8) Phase 2 epic | Delivery | Incomplete until #18–#19 | #17 done | Open | — | — | — | — | #18 #19 | Continue children |

## Work completed this cycle

- PR #27 merged to `main`; post-merge CI success.
- #17 closed; Phase 0 (#6) and Phase 1 (#7) closed.
- #18 implemented on `fix/issue-18-subtree-limits`.

## Required human decisions

1. Enable branch protection and required CI on `main` (#21).
2. Whether to add an FTC SDK / Android compile job (audit F-TEST-004).
3. Do not merge Dependabot PRs #4, #28–#30 without the analysis in #22.
