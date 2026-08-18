# Examples

These sketches show the Phase 0–2 student API. They are **not** robot OpModes and they do **not** command hardware.

## Compilable path (CI)

Open and run this test class. `./gradlew check` compiles and executes it on Ubuntu and Windows:

- [`src/test/java/org/allsparks/helm/examples/Phase0DescribeExampleTest.java`](../src/test/java/org/allsparks/helm/examples/Phase0DescribeExampleTest.java)

```powershell
.\gradlew.bat test --tests org.allsparks.helm.examples.Phase0DescribeExampleTest
```

On Linux/macOS:

```bash
./gradlew test --tests org.allsparks.helm.examples.Phase0DescribeExampleTest
```

That file is the source of truth for the student API sketch (goal, task with completion, intent tree with timed actions and an explicit `safeTerminal`, desktop evaluation, and validation). Phase 2 rejects ACTION nodes that have no timeout. Do not copy a second sketch here — it will drift.

Related tests (behavior, not the student example):

- `org.allsparks.helm.HelmEligibilityTest#studentApiExampleCompilesAndEvaluates`
- `org.allsparks.helm.intent.IntentTreeBehaviorTest`

Default `Helm.create()` is mode `OFF` and `allowsPhysicalOutput()` is always false in this scaffold.
