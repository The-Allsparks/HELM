# Examples

These sketches show the Phase 0–2 student API. They are **not** robot OpModes and they do **not** command hardware.

See unit tests:

- `org.allsparks.helm.HelmEligibilityTest#studentApiExampleCompilesAndEvaluates`
- `org.allsparks.helm.intent.IntentTreeBehaviorTest`

```java
Goal scorePreload = Goal.named("ScorePreload");

Task scoreTask = Task.builder("ScorePreload")
    .requires(Capability.DRIVE_TRANSLATION)
    .requires(Capability.LOW_SCORING)
    .timeout(Duration.ofSeconds(6))
    .fallback("ParkSafely")
    .completion(Condition.snapshotFact("preloadScored"))
    .build();

IntentTree autonomous = IntentTree.named("SimpleAutonomous")
    .sequence(
        IntentTree.condition("PreflightReady"),
        IntentTree.action("ScorePreload"),
        IntentTree.fallback(
            IntentTree.action("AcquireNearestPiece"),
            IntentTree.action("ParkSafely")
        )
    );

TaskEvaluation evaluation = helm.evaluate(scoreTask, worldSnapshot);
DecisionRecord recommendation = helm.recommend(List.of(scoreTask), worldSnapshot);
```

`recommendation` explains that shadow selection is not enabled. Default `Helm.create()` is mode `OFF`.
