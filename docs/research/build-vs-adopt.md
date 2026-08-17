# Build versus adopt

Access date: **2026-08-17**. Citations use the [source register](source-register.md).

## Question

Should HELM:

1. Adopt an existing FTC command or behavior framework?
2. Extend an existing framework?
3. Build a small independent coordination core with adapters?
4. Use a hybrid approach?

## Candidates

| Candidate | FTC / Android | License | Maintained? | Command composition | BT-like | Replay | Student fit | Core dependency? |
|-----------|---------------|---------|-------------|---------------------|---------|--------|-------------|------------------|
| NextFTC | Yes | **GPL-3.0** (S19) | Active docs | Yes (S18) | No | No | High, Kotlin | **No** — copyleft |
| FTCLib | Yes | SPDX unasserted (S23) | Command samples stale (2022) | Yes (S22) | No | No | Medium | **No** — whole robot framework |
| Dairy/Mercurial | Yes | BSD-family README (S26) | Docs current | Yes (S25) | No | No | Medium-high | **No** — runtime ecosystem |
| Ivy | Yes | BSD-3-Clause (S14) | Active with Pedro | Yes (S11–S13) | No | No | High if team uses Pedro | Adapter later, not core |
| Road Runner Actions | Yes | MIT (S16) | Docs current | Sequential/parallel (S15) | No | No | High | Adapter later, not core |
| Pedro Pathing | Yes | BSD-3-Clause (S10) | Active | Path following, not HELM | No | No | Required chassis | Adapter later, not core |
| WPILib commands | FRC only | WPILib | Authoritative FRC | Yes (S1–S2) | No | Via AdvantageKit | Conceptual | **No** — not Control Hub |
| PathPlanner | FRC | PathPlanner | Active | AutoBuilder + pathfind (S3) | No | AK note (S3) | Conceptual | **No** |
| AdvantageKit | FRC | SPDX unasserted (S6) | Active | Logging/replay (S4–S5) | No | **Yes** | Conceptual for TRACE | **No** |
| BehaviorTree.CPP | C++ / ROS-adjacent | MIT (S29) | Active | Full BT (S27–S28) | **Yes** | Visualizer upstream | Too foreign | **No** — do not import |
| Nav2 BT | ROS 2 | Nav2 | Active | XML trees + recovery (S30) | **Yes** | ROS tooling | Too foreign | **No** |

## Evaluation against HELM needs

HELM needs: explainable eligibility, unknown/stale conditions, semantic capabilities, logical resources that do not fight a scheduler, bounded retries, timeouts, intent-tree vocabulary, TRACE records, replay isolation, season-strategy separation, and a deterministic disable path.

No candidate provides that combination on the FTC Control Hub.

Command frameworks **execute** actions with subsystem requirements (S1, S12, S15, S18, S21). HELM **coordinates** among permitted actions. Adopting a scheduler as HELM would either:

- hide HELM inside someone else's interruption policy, or
- create two schedulers.

BehaviorTree.CPP and Nav2 provide the right *ideas* (fallback, recovery, reactive restart) and the wrong *runtime* (C++ / ROS 2 on hardware Allsparks does not ship).

NextFTC's GPL-3.0 license (S19) is incompatible with keeping HELM MIT without turning the library copyleft.

## Decision

**Build a small deterministic HELM core, surrounded by optional adapters.**

This is option 3, with a **later hybrid** for adapters only (option 4 at the boundary, not in core):

- Core: pure Java 11, `org.allsparks.helm`, no FTC SDK, no Pedro, no command framework.
- Adapters (future, out of core compile graph): Ivy/Pedro, Road Runner Actions, MIMIC, ViDAR conditions, AMPER envelope, BEACON capabilities, TRACE recorder.
- Explicit non-adapters in this scaffold: NextFTC (license), Dairy (ecosystem lock-in), BehaviorTree.CPP (language), AdvantageKit (FRC).

HELM must not replace Pedro, Road Runner, NextFTC, FTCLib, Dairy, or Ivy. It coordinates through them where practical.

## Student impact

Students learn HELM vocabulary (goal, task, condition, capability) without first adopting a foreign framework. Command-framework skills remain useful at the adapter boundary.

## Safety impact

A small core can refuse physical output in every mode. An adopted scheduler is designed to run hardware commands; wrapping it as “HELM” would make “OFF” harder to guarantee.

## Revisit

Revisit if Allsparks standardizes on one command framework *and* that framework gains first-class unknown/stale conditions, capability objects, and replay-safe clocks. Even then, prefer an adapter over a merge.
