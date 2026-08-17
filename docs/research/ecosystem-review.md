# Ecosystem review

Access date for all fetches in this document: **2026-08-17**.

This review prefers primary documentation and public source. Search-result summaries were used only as pointers; claims below are tied to pages that were fetched or to GitHub repository metadata retrieved with `gh api`.

Legend:

- **Fact:** stated by the cited source.
- **Inference:** engineering conclusion drawn by The Allsparks, not a vendor claim.

---

## 1. FRC command-based programming

### CommandScheduler resource requirements and interruption

**Fact.** WPILib documents that `CommandScheduler` runs once per iteration (ordinarily 20 ms), polls triggers, runs scheduled commands, and ends finished or interrupted commands. `schedule()` verifies a command is not already in a composition; if requirements are in use, interruptible commands are cancelled, otherwise the incoming command is not scheduled. Interruption behavior `kCancelIncoming` prevents the new command from replacing the current one.

Source: [The Command Scheduler](https://docs.wpilib.org/en/stable/docs/software/commandbased/command-scheduler.html) (WPILib docs, accessed 2026-08-17). The page includes Java source excerpts from `CommandScheduler.schedule` and `run`.

**Inference.** HELM must not become a second scheduler that fights WPILib-style or FTC command schedulers. Logical HELM resources should complement subsystem requirements, not re-implement interruption.

### Command compositions

**Fact.** WPILib command compositions include sequential, parallel, parallel-race, and parallel-deadline groups, plus conditional commands. Commands in a composition must not be independently scheduled.

Source: [Command Compositions](https://docs.wpilib.org/en/stable/docs/software/commandbased/command-compositions.html) (WPILib docs, accessed 2026-08-17).

**Inference.** Sequence / parallel / race / deadline are the composition primitives students already meet in FRC-style frameworks. HELM intent-tree nodes should stay recognizable without importing WPILib.

### Autonomous choosers

**Fact.** WPILib teams commonly select a pre-authored autonomous with `SendableChooser`. This is a static choice, not runtime task scoring.

Source: WPILib command-based documentation set at [docs.wpilib.org command-based index](https://docs.wpilib.org/en/stable/docs/software/commandbased/index.html) (accessed 2026-08-17). Chooser behavior is the documented dashboard pattern; HELM treats it as the Phase 3 analog (student-authored static plan).

---

## 2. PathPlanner, Choreo, and FRC pathfinding

### Pathfinding and dynamic obstacles

**Fact.** PathPlannerLib includes commands that plan between two points while avoiding field obstacles using a `navgrid.json`. `AutoBuilder.pathfindToPose` and `AutoBuilder.pathfindThenFollowPath` are documented. Dynamic obstacles can be supplied as bounding boxes. Heading at start and end is not fully controllable; AD* may refine the path while the robot is moving, which can change direction. AdvantageKit compatibility requires `Pathfinding.setPathfinder(new LocalADStarAK())`.

Source: [Pathfinding](https://pathplanner.dev/pplib-pathfinding.html) (PathPlanner docs, last modified 12 January 2026, accessed 2026-08-17).

**Inference.** Dynamic pathfinding is a **lower-layer** chassis capability. HELM should request “go to pose / follow path” through an adapter, not embed AD*.

### Choreo

**Fact.** The same PathPlanner page links “Choreo Interop” as adjacent documentation. Choreo is an FRC trajectory optimization tool used with PathPlanner; it is not an FTC Control Hub library.

Source: [Pathfinding](https://pathplanner.dev/pplib-pathfinding.html) footer (“Choreo Interop”), accessed 2026-08-17.

**Inference.** Do not adopt Choreo into HELM. Transfer the lesson that precise final approach is often a pre-planned path chained after a coarse pathfind.

---

## 3. AdvantageKit deterministic replay

**Fact.** AdvantageKit (Team 6328) records **all inputs** flowing into robot code so the full logic can be replayed in simulation from a log. It is explicitly a logging/replay framework, not a task planner. Replay requires deterministic, synchronized data sources. Documented non-deterministic hazards include unordered maps, unseeded randomness, filesystem reads, NetworkTables-as-input, and raw FPGA timestamps.

Sources:

- [What is AdvantageKit?](https://docs.advantagekit.org/getting-started/what-is-advantagekit/) (accessed 2026-08-17)
- [Non-Deterministic Data Sources](https://docs.advantagekit.org/getting-started/common-issues/non-deterministic-data-sources) (accessed 2026-08-17)
- Repository: [Mechanical-Advantage/AdvantageKit](https://github.com/Mechanical-Advantage/AdvantageKit) (accessed 2026-08-17)

**Inference.** HELM replay should re-evaluate decisions from recorded world snapshots, strategy config, and clock — not from live hardware. Replay must never create physical outputs. HELM should not depend on AdvantageKit; TRACE is the Allsparks recording project, and AdvantageKit is WPILib/FRC-specific.

---

## 4. Team 254 public robot code

**Fact.** Team 254's 2025 public code (`FRC-2025-Public`, MIT) describes:

- a `SuperstructureStateMachine` coordinating multi-step subsystem actions
- `ModalControls` switching driver/robot behavior by objective (CORAL, ALGAECLIMB, CORALMANUAL)
- `CoralStateTracker` monitoring game-piece location through intake → indexer → claw
- AdvantageKit IO interfaces per subsystem
- automatic scoring height selection based on reef branch availability (season-specific)

Source: [Team254/FRC-2025-Public README](https://github.com/Team254/FRC-2025-Public) (accessed 2026-08-17).

**Fact.** The 2024 public code used controller modes (SPEAKER, HP, CLIMB, etc.) and a `RobotState` pose/frame tracker.

Source: [Team254/FRC-2024-Public](https://github.com/Team254/FRC-2024-Public) (accessed 2026-08-17).

**Inference.** Transferable lessons: explicit modes, a single robot-state snapshot, superstructure coordination **below** high-level selection, and possession tracking as a first-class condition. Do not copy season scoring policy into HELM core. 254's automatic L1–L4 selection is **season strategy**, which HELM must keep outside core.

---

## 5. FTC pathing and actions

### Pedro Pathing

**Fact.** Pedro Pathing is a Bézier / PIDF path follower for omnidirectional FTC robots. It requires Android Studio (not OnBot Java or Blocks), localization, and tuning. Paths can be created on the fly because the follower uses PID toward a pose.

Source: [Pedro Pathing introduction](https://pedropathing.com/docs/pathing) (accessed 2026-08-17). Repository license: **BSD-3-Clause** (`Pedro-Pathing/PedroPathing`, `gh api`, 2026-08-17).

**Inference.** Pedro is the Allsparks chassis layer. HELM must not generate wheel powers. A future `PedroActionAdapter` should consume action state, cancellation, and pose confidence — fields that still need a readiness check.

### Ivy (Pedro command framework)

**Fact.** Ivy is a command-based control-flow library. Commands have `start()`, `execute()`, `done()`, and `end(endCondition)`. `Scheduler.execute()` must be called once per loop. `Scheduler.cancel` / `Scheduler.reset` are documented. Requirements plus integer priorities define interrupt, block, and conflict behavior (`END`/`SUSPEND`, `CANCEL`/`QUEUE`, `CANCEL`/`QUEUE`/`OVERRIDE`).

Sources:

- [What are commands?](https://pedropathing.com/docs/ivy/what-are-commands) (accessed 2026-08-17)
- [Scheduling and OpMode use](https://pedropathing.com/docs/ivy/creating-opmodes) (accessed 2026-08-17)
- [Requirements and Priorities](https://pedropathing.com/docs/ivy/requirements-and-priorities) (accessed 2026-08-17)
- Repository: [Pedro-Pathing/Ivy](https://github.com/Pedro-Pathing/Ivy), license **BSD-3-Clause** (accessed 2026-08-17)

**Inference.** If Allsparks adopts Ivy, HELM should schedule Ivy commands through an adapter rather than replace `Scheduler`. HELM resource claims must not contradict Ivy priorities.

### Road Runner Actions

**Fact.** Road Runner v1 Actions are cooperative long-running steps with `boolean run(TelemetryPacket)`. Built-ins include `SequentialAction`, `ParallelAction`, `SleepAction`, trajectory follow, and turn. `Actions.runBlocking` remains interruptible by the stop button unless a custom action blocks with `Thread.sleep` or a tight `while`. Calls to `run()` should complete quickly; delays over 100 ms starve peers.

Source: [Actions | Road Runner Docs](https://rr.brott.dev/docs/v1-0/actions/) (accessed 2026-08-17). Repository [acmerobotics/road-runner](https://github.com/acmerobotics/road-runner) license **MIT**.

**Inference.** HELM leaf actions must be non-blocking in the OpMode loop. Road Runner is an optional adapter target, not a core dependency. Allsparks currently standardizes on Pedro, so Road Runner adapters are not implemented in this scaffold.

---

## 6. FTC command frameworks

### NextFTC

**Fact.** NextFTC is a Kotlin command-based framework with commands, subsystems, and components, plus an optional hardware module. Documented groups: `SequentialGroup`, `ParallelGroup`, `ParallelRaceGroup`, `ParallelDeadlineGroup`. Maven coordinates `dev.nextftc:ftc:1.1.0` appear in the library docs.

Sources:

- [NextFTC documentation](https://nextftc.dev/nextftc/) (accessed 2026-08-17)
- [Command Groups](https://nextftc.dev/nextftc/commands/groups) (accessed 2026-08-17)
- Repository [NextFTC/NextFTC](https://github.com/NextFTC/NextFTC) license **GPL-3.0** (`gh api`, 2026-08-17)

**Inference.** GPL-3.0 is **not compatible** with shipping NextFTC inside an MIT HELM core. An optional out-of-tree adapter in a GPL robot project could exist later; HELM core must not depend on NextFTC.

### FTCLib

**Fact.** FTCLib provides a WPILib-styled command-based framework for FTC, including `CommandOpMode` and command groups. Sample repository last push was 2022-02-22.

Sources:

- [FTCLib docs](https://docs.ftclib.org/) (accessed 2026-08-17)
- [Robot and CommandOpMode](https://docs.ftclib.org/ftclib/command-base/command-system/robot-and-commandopmode) (accessed 2026-08-17)
- [Command groups](https://docs.ftclib.org/ftclib/command-base/command-system/command-groups) (accessed 2026-08-17)
- [FTCLib/FTCLib](https://github.com/FTCLib/FTCLib) license metadata `NOASSERTION` (`gh api`, 2026-08-17)

**Inference.** FTCLib is a whole robot framework. Adopting it as HELM would duplicate scheduling and pull hardware utilities HELM must not own. Maintenance of the command-sample repo appears stale relative to 2026; treat FTCLib as an optional adapter, not a core.

### Dairy / Mercurial

**Fact.** Dairy is an FTC library ecosystem. Mercurial is its command-based layer, described as lower-boilerplate than FTCLib, with a unit-tested scheduler. Dairy's README states Dairy code uses the same BSD 3-Clause Clear family as the FTC SDK (`LICENSE.dairy`).

Sources:

- [Dairy introduction](https://docs.dairy.foundation/introduction) (accessed 2026-08-17)
- [Mercurial overview](https://docs.dairy.foundation/Mercurial/overview) (accessed 2026-08-17)
- [Dairy-Foundation/Dairy](https://github.com/Dairy-Foundation/Dairy) (accessed 2026-08-17)

**Inference.** Mercurial is a command runtime. HELM should not replace it. No Dairy compile dependency in core.

---

## 7. Broader robotics (transfer lessons only)

### BehaviorTree.CPP

**Fact.** BehaviorTree.CPP v4.8 documents Sequence, AsyncSequence, SequenceWithMemory, ReactiveSequence, Fallback, AsyncFallback, and ReactiveFallback. Sequence ticks children while they return SUCCESS and aborts on FAILURE. Fallback tries children until one SUCCESS. Reactive variants restart from the first child when a child is RUNNING. The library is C++ (MIT).

Sources:

- [Sequences](https://www.behaviortree.dev/docs/nodes-library/SequenceNode) (v4.8, accessed 2026-08-17)
- [Fallbacks](https://www.behaviortree.dev/docs/nodes-library/FallbackNode) (v4.8, accessed 2026-08-17)
- [BehaviorTree/BehaviorTree.CPP](https://github.com/BehaviorTree/BehaviorTree.CPP) license **MIT**

**Inference.** Import the **vocabulary** (sequence, fallback, reactive restart, memory) as Java intent-tree nodes. Do **not** import the C++ runtime, XML factory, or blackboard implementation onto the Control Hub.

### Nav2 behavior trees

**Fact.** Nav2 documents XML behavior trees for navigation. A minimal tree recomputes a path every 1 m and follows it. Nav2 notes that this minimal tree has **no recovery methods, no retries on failure, and no selected planner**. Other provided trees add recovery and replanning. Nav2 is ROS 2, not FTC.

Source: [Nav2 Behavior Trees](https://docs.nav2.org/behavior_trees/index.html) (Nav2 1.0.0 docs, accessed 2026-08-17).

**Inference.** Recovery, bounded retry, and a safe terminal action are not optional decorations; Nav2 calls out their absence as a limitation of the simplest tree. HELM Phase 2 validation requires timeouts, bounded retries, and a safe terminal.

---

## 8. FTC Control Hub constraints

**Fact.** Pedro Pathing requires Android Studio and does not support OnBot Java or Blocks. Road Runner Actions warn that `run()` must return quickly. FTC OpModes run on the REV Control Hub (Android).

Sources: Pedro introduction and Road Runner Actions pages cited above.

**Inference.** HELM evaluation must be bounded, allocation-aware, non-blocking, and free of file/network I/O on the decision path. Java 11 matches Allsparks AMPER/MIMIC library convention.

---

## 9. What this review does not claim

- This review does **not** claim Pedro currently exposes cancellation + pose confidence in a HELM-ready API. That is a readiness-gate item.
- This review does **not** claim TRACE already records unified decision events. The TRACE repository was empty on 2026-08-17.
- This review does **not** claim any public FTC library is a drop-in explainable task selector with unknown/stale conditions and capability-aware degradation.
