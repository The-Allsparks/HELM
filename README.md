# HELM

**High-level Execution and Logic Manager for FTC**

HELM is an experimental high-level behavior-coordination layer for FIRST Tech Challenge robots. It uses trusted state and capability information from lower-level systems to validate goals, select tasks, coordinate execution, choose bounded fallbacks, and explain every decision.

HELM **does not** directly control motors, servos, or other robot hardware. It coordinates existing capabilities through adapters and command interfaces.

---

## Read this first

| Fact | Status |
|------|--------|
| **HELM is experimental** | Yes. Do not treat this repository as a competition-ready planner. |
| **HELM is not required** to operate an FTC robot | A conventional OpMode, Pedro path, or static auto must keep working with HELM off. |
| **HELM does not replace lower-layer safety** | MIMIC, AMPER, BEACON, the FTC SDK, and the robot application always override HELM. |
| **HELM does not directly control hardware** | There is no motor or servo output in this library. |
| **Initial phases are passive** | Phase 0 describes intent. Phase 1 observes. Phase 2 validates. None of these command the robot. |
| **Dynamic authority is not approved** | Phase 3 static execution and Phase 5 bounded substitution require explicit later gates. |
| **Every advanced feature can be disabled** | Default mode is `OFF`. Feature flags default off. The operator disable path is honored. |
| **This is a student learning project** | Students must be able to explain the current phase before enabling the next one. |

**Do not place HELM in control of a competition robot before its lower-layer dependencies are reliable.** Opening this repository, cloning it, or merging a pull request is not authorization to enable HELM on a robot.

---

## Built by The Allsparks

HELM is created and maintained by **[The Allsparks](https://github.com/The-Allsparks)** (FTC Team **#36117**).

It is the last major layer in the team's robot-software architecture:

```text
ViDAR reports the environment.
Pose estimation reports location and confidence.
BEACON reports health and available capabilities.
AMPER reports the available performance envelope.
HELM validates or selects a task.
Pedro moves the chassis.
MIMIC operates coordinated mechanisms.
TRACE records the complete decision chain.
```

Repository: **[The-Allsparks/HELM](https://github.com/The-Allsparks/HELM)**

> **Disclaimer:** HELM is community-developed and unofficial. It is **not** affiliated with or endorsed by FIRST, REV Robotics, Pedro Pathing, WPILib, or other referenced vendors. Teams must verify legality and performance against the current-season FTC Game Manual.

---

## Current status

| Item | Status |
|------|--------|
| **Version** | `0.1.0-SNAPSHOT` |
| **Implemented phases** | **Phase 0** (vocabulary), **Phase 1** (passive observation), **Phase 2** (offline validation), plus desktop-only simulated tree walking |
| **Phase 3 static execution** | Designed; **not approved**; physical output refused |
| **Phases 4–9** | Designed / experimental / **disabled** |
| **Hardware control** | **Disabled.** HELM cannot command motors or servos. |
| **Hardware validation** | **None.** This scaffold has not run on a Control Hub or robot. |

Supported targets for this scaffold:

* **FTC SDK:** current public [FtcRobotController](https://github.com/FIRST-Tech-Challenge/FtcRobotController) season releases for later TeamCode integration. Desktop tests compile against Java 11 **without** the SDK on the classpath.
* **Library build:** Java 11 source/target; CI uses Temurin 17 to compile and test.
* **Hardware:** none in this phase. Adapters are no-ops.

### Current limitations

* TRACE is a sibling project and was empty when HELM was scaffolded. HELM ships a no-op TRACE sink. Active authority must not be enabled until TRACE records unified timestamps and decision events.
* BEACON, MIMIC, AMPER, Pedro, and ViDAR do not yet expose every readiness-gate field HELM will eventually consume. Gaps are documented in [docs/readiness-gates.md](docs/readiness-gates.md).
* Simulated tree walking is for desktop tests. It is not robot execution.
* Season scoring policy, field geometry, and point values are **out of** HELM core.

---

## Learning progression

```text
Describe → Observe → Validate → Explain → Execute → Recover → Select → Predict
```

Every phase is independently useful, optional, feature-selectable, TRACE-observable, student-readable, reversible, and disableable. Students must understand the current phase before enabling the next one.

See [docs/student-learning-path.md](docs/student-learning-path.md) and [docs/mentor-guide.md](docs/mentor-guide.md).

---

## Quick start (desktop)

```powershell
git clone https://github.com/The-Allsparks/HELM.git
cd HELM
.\gradlew.bat test
```

On Linux/macOS:

```bash
./gradlew test
```

Phase 0 student API:

```java
Goal scorePreload = Goal.named("ScorePreload");

Task scoreTask = Task.builder("ScorePreload")
    .requires(Capability.DRIVE_TRANSLATION)
    .requires(Capability.LOW_SCORING)
    .timeout(Duration.ofSeconds(6))
    .fallback("ParkSafely")
    .build();

TaskEvaluation evaluation = helm.evaluate(scoreTask, worldSnapshot);
telemetry.addData("Eligible", evaluation.isEligible());
telemetry.addData("Reason", evaluation.explanation());
```

HELM remains `OFF` unless the robot application sets a mode. Installing this library does nothing by itself.

---

## Documentation

| Doc | Purpose |
|-----|---------|
| [Architecture](docs/architecture.md) | Four-layer model and package map |
| [Responsibility boundaries](docs/responsibility-boundaries.md) | What HELM owns and does not own |
| [World snapshot](docs/world-snapshot.md) | Immutable timestamped inputs |
| [Goals and tasks](docs/goals-and-tasks.md) | Eligibility language |
| [Intent trees](docs/intent-trees.md) | Inspectable behavior composition |
| [Conditions and confidence](docs/conditions-and-confidence.md) | Unknown and stale are not booleans |
| [Capabilities](docs/capabilities.md) | Semantic availability |
| [Resources](docs/resources.md) | Logical exclusion, not MIMIC interlocks |
| [Recovery](docs/recovery.md) | Action / task / plan / safety |
| [Decision explanations](docs/decision-explanations.md) | Why a choice was made |
| [TRACE integration](docs/trace-integration.md) | Decision evidence chain |
| [Replay](docs/replay.md) | Deterministic re-evaluation |
| [Season strategy](docs/season-strategy.md) | Keep game policy out of core |
| [Performance](docs/performance.md) | Control Hub budgets |
| [Safety](docs/safety.md) | Non-negotiable constraints |
| [Readiness gates](docs/readiness-gates.md) | Why active control is refused |
| [Student learning path](docs/student-learning-path.md) | Phase objectives |
| [Mentor guide](docs/mentor-guide.md) | How to teach HELM |
| [Troubleshooting](docs/troubleshooting.md) | Common failure modes |
| [Ecosystem review](docs/research/ecosystem-review.md) | Source-backed FTC/FRC research |
| [Build vs adopt](docs/research/build-vs-adopt.md) | Why HELM is a small independent core |
| [ADRs](docs/adr/README.md) | Architecture decisions |

---

## Design principles

1. **Passive first.** Describe, observe, and validate before any execution authority.
2. **Feature-flagged phases.** Each phase is independently testable and reversible.
3. **Fail safe.** Unknown, stale, and missing inputs block tasks that require certainty.
4. **Do not replace lower layers.** Pedro, MIMIC, AMPER, BEACON, ViDAR, and TRACE keep their jobs.
5. **Explain every decision.** Automation is not intelligence.
6. **Honest maturity.** Do not advertise adaptive autonomous operation before approval gates pass.

---

## License

MIT — same open-source license family as [ViDAR](https://github.com/The-Allsparks/ViDAR), [AMPER](https://github.com/The-Allsparks/AMPER), [MIMIC](https://github.com/The-Allsparks/MIMIC), and [BEACON](https://github.com/The-Allsparks/BEACON). See [LICENSE](LICENSE).

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md), [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md), and [SECURITY.md](SECURITY.md).
