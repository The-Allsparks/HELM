# Readiness gates

Before creating an **active** HELM integration, verify each item. Status on **2026-08-17**:

| Gate | Status | Evidence |
|------|--------|----------|
| TRACE records unified timestamps and decision events | **Not met** | `The-Allsparks/TRACE` was an empty repository |
| Pedro exposes action state, cancellation, outcome, and pose confidence | **Unknown / not wrapped** | Pedro docs confirm following, on-the-fly pose, Ivy cancel; HELM-ready pose-confidence API not verified in an Allsparks adapter |
| ViDAR exposes timestamped observations and confidence | **Partial** | ViDAR exists and models observations; HELM snapshot contract is defined, no live adapter |
| MIMIC exposes readiness, completion, cancellation, explicit failure | **Not met for authority** | MIMIC Phase 0 scaffold / draft PR; no hardware validation |
| AMPER exposes an available performance envelope | **Partial** | AMPER Phase 0/1 library exists; not hardware-validated; envelope not consumed by HELM |
| BEACON exposes capability health and safety restrictions | **Not met** | BEACON README stub only |
| Every delegated action has timeout, completion, cancellation | **Not met** | No delegated live actions |
| A complete autonomous works without HELM | **Team process** | Required; not demonstrated in this repo |
| That autonomous is recorded through TRACE | **Not met** | TRACE empty |
| Simulation or replay can reproduce relevant decisions | **Partial** | Desktop unit replay of eligibility only |
| Students can explain the conventional autonomous | **Team process** | Required before Phase 3 |
| HELM can be disabled without modifying subsystem code | **Met in library** | Default `OFF`; no subsystem hooks shipped |

Because these gates are not met, HELM must remain in `OFF`, `OBSERVE`, or `VALIDATE`. Creating this repository documents the coordinator; it does **not** authorize robot control.

## Repository-creation judgment

Creating a public HELM repository **is appropriate now** because:

- The work is research, contracts, Phase 0–2 passive code, and governance.
- Defaults refuse execution.
- README and ADRs state that HELM is the last layer and must not run a competition robot yet.

Creating the repository would **encourage premature deployment** if execute adapters or “enable in TeleOp” samples were shipped. Those are excluded.
