# Team conventions

Inspected 2026-08-17: ViDAR, AMPER, MIMIC, BEACON, TRACE.

## Preserved from AMPER (current Allsparks Java library standard)

- MIT license, copyright The Allsparks (FTC Team 36117)
- Package `org.allsparks.<project>`
- Java 11 source/target, CI Temurin 17
- Gradle 8.7 wrapper, JUnit 5
- LF via `.gitattributes`, `.editorconfig` indent 4 Java / 2 yaml
- Contributor Covenant, SECURITY, CITATION.cff, Keep a Changelog
- Phase feature flags defaulting intervention/execution off
- GitHub Actions `check` on ubuntu+windows, docs-structure job
- Issue templates: bug, feature, phase_work
- No secrets in CI

## Not copied from ViDAR

- `org.firstinspires.ftc.teamcode` packaging (ViDAR's FTC-app layout)
- Python host stack and Docker
- Android stubs on the library classpath

## Not copied from incomplete siblings

- TRACE was empty — HELM does not pretend TRACE APIs exist
- BEACON README-only — HELM uses snapshot-fed capability states
- MIMIC draft Phase 0 — no mechanism command adapter

## Intentional HELM deviations

See [architecture.md](architecture.md). Modes plus phases; no REV observers; deterministic snapshot ids.
