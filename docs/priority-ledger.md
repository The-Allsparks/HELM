# HELM priority ledger

Maintained by the repository orchestrator. Source of findings: [initial deep audit](audits/initial-deep-audit.md) (commit `cb214b2e080ccc34d5536b5c0434a0c7cb143bd5`, 2026-08-17).

**Automatic merge:** false. **Max active implementation subagents:** 1. **Identity:** TA-C-GHill.

Priority model (highest first): safety blockers → correctness blockers → CI/build failures → issues that unblock many others → architectural seams → tests for upcoming work → small user-facing slices → measured performance → docs/usability → optional advanced capabilities → cosmetic cleanup.

## Current stream

| Field | Value |
|-------|--------|
| Open implementation PR to `main` | Draft [#16](https://github.com/The-Allsparks/HELM/pull/16) |
| Selected issue | [#17](https://github.com/The-Allsparks/HELM/issues/17) explicit safe-terminal marking |
| Why selected | Highest-priority **ready** safety/correctness defect; no hardware; unblocks honest Phase 2 |
| Subagent | Single-use review/implement for #17 |
| Merge status | Not authorized without human approval (`AUTOMATIC_MERGE=false`) |

## Ledger

| Issue | Priority | Readiness | Dependencies | Status | Assigned subagent | Branch | Pull request | CI | Merge | Blocker | Next action |
|-------|----------|-----------|--------------|--------|-------------------|--------|--------------|----|-------|---------|-------------|
| [#17](https://github.com/The-Allsparks/HELM/issues/17) explicit safe terminal | 1 | Ready | Scaffold in #16 | Selected | pending | `fix/issue-17-explicit-safe-terminal` | stacked on #16 stream | — | — | None | Review scope; implement smallest slice |
| [#18](https://github.com/The-Allsparks/HELM/issues/18) subtree limits | 2 | Ready after #17 preferred | #8 / #17 fixtures | Pending | — | — | — | — | — | Prefer #17 first | Wait |
| [#19](https://github.com/The-Allsparks/HELM/issues/19) skipped validation not valid | 3 | Ready | Scaffold | Pending | — | — | — | — | — | One issue at a time | After #17 |
| [#21](https://github.com/The-Allsparks/HELM/issues/21) branch protection | 4 | Blocked | Human org settings | Blocked | — | n/a | n/a | n/a | n/a | Maintainer policy | Human enablement |
| [#22](https://github.com/The-Allsparks/HELM/issues/22) Dependabot majors | 5 | Ready (analysis) | None | Do not merge #1–#4 | — | dependabot/* | #1–#4 | — | forbidden until analysis | Compatibility | Comment/close majors |
| [#23](https://github.com/The-Allsparks/HELM/issues/23) README completion | 6 | Ready | None | Pending | — | — | — | — | — | None | After validator honesty |
| [#20](https://github.com/The-Allsparks/HELM/issues/20) walker subtrees | 7 | Blocked | #18 | Blocked | — | — | — | — | — | #18 | Wait |
| [#24](https://github.com/The-Allsparks/HELM/issues/24) future timestamps | 8 | Ready | None | Pending | — | — | — | — | — | None | Later |
| [#26](https://github.com/The-Allsparks/HELM/issues/26) examples path | 9 | Ready | Scaffold | Pending | — | — | — | — | — | None | Later |
| [#25](https://github.com/The-Allsparks/HELM/issues/25) desktop characterization | 10 | Ready (desktop) | None | Pending | — | — | — | — | — | Do not claim Control Hub | Later |
| [#9](https://github.com/The-Allsparks/HELM/issues/9)–[#15](https://github.com/The-Allsparks/HELM/issues/15) Phase 3+ | 13+ | Blocked | Readiness gates | Blocked | — | — | — | — | — | `docs/readiness-gates.md` | Do not implement |
| [#6](https://github.com/The-Allsparks/HELM/issues/6) [#7](https://github.com/The-Allsparks/HELM/issues/7) [#8](https://github.com/The-Allsparks/HELM/issues/8) | Delivery | #8 incomplete until #17–#19 | None | Open; #8 is epic | — | `feature/phase-0-passive-scaffold` | #16 | success | waiting human | #17–#19 | Fix HIGH findings before treating #8 complete |

## Work completed this cycle

- Deep audit: `docs/audits/initial-deep-audit.md`.
- Labels: `severity:*`, `type:*`, `epic`, `ready`.
- Milestones: Passive-core correctness (#11), Repository health (#12).
- Issues #17–#26 filed from audit findings.

## Required human decisions

1. Merge authorization for PR #16 after HIGH Phase 2 defects are addressed or explicitly deferred.
2. Enable branch protection and required CI on `main` (#21).
3. Whether to add an FTC SDK / Android compile job (audit F-TEST-004; not filed as ready).
4. Do not merge Dependabot PRs #1–#4 without the analysis in #22.
