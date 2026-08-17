# Resources

HELM prevents logically incompatible **tasks** from being declared together (Phase 2 parallel validation). It is not a second command scheduler.

Well-known resources: `DRIVETRAIN`, `INTAKE`, `ELEVATOR`, `ARM`, `SCORING_EFFECTOR`, `VISION_AIM`, `POWER_BURST`, `OPERATOR_AUTHORITY`.

`EXCLUSIVE` resources may appear once in a parallel node. `SHAREABLE` resources may repeat.

MIMIC remains responsible for physical mechanism interlocks. Ivy/WPILib-style subsystem requirements remain responsible for runtime interruption if those frameworks are used. HELM claims complement that protection.

Acquisition order, preemption, and cancellation cleanup for live execution are Phase 3+ and are not enabled.
