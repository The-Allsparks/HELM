# Contributing to HELM

HELM is maintained by [The Allsparks](https://github.com/The-Allsparks) (FTC Team 36117) for our team and the wider FTC community.

## Setup

```powershell
git clone https://github.com/The-Allsparks/HELM.git
cd HELM
.\gradlew.bat test
```

## Rules of engagement

1. **Do not enable robot execution** in pull requests. Phase 3+ requires an explicit approval gate.
2. Phase 0, Phase 1, and Phase 2 must remain free of motor and servo commands.
3. Distinguish **verified fact**, **engineering inference**, and **untested hypothesis** in documentation.
4. Never describe an FRC, ROS, or BehaviorTree.CPP capability as a current FTC HELM capability without evidence.
5. Do not commit secrets, Wi-Fi passwords, tokens, or student PII.
6. Do not add compile dependencies on Pedro, NextFTC, FTCLib, Dairy, MIMIC, AMPER, BEACON, ViDAR, or TRACE in HELM core.
7. Keep season point values and field geometry out of `org.allsparks.helm`.

## Pull requests

- Prefer small, reviewable PRs.
- Include motivation, phase impact, test evidence, and safety notes.
- Use draft pull requests for incomplete architecture, validation, or safety work.
- Use `Closes #<issue>` only when the pull request fully resolves that issue.
- Run `.\gradlew.bat test` (or `./gradlew test`) before requesting review.

## Line endings

The repository stores LF line endings (see [.gitattributes](.gitattributes)).

## License

Contributions are accepted under the MIT License ([LICENSE](LICENSE)). No CLA is required.
