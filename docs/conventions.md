# Team conventions

Inspected 2026-08-17: ViDAR, AMPER, MIMIC, BEACON, TRACE.

## Preserved from AMPER (current Allsparks Java library standard)

- MIT license, copyright The Allsparks (FTC Team 36117)
- Package `org.allsparks.<project>`
- Java 11 source/target, CI Temurin 17
- Gradle 8.7 wrapper, JUnit 5 (Dependabot majors for these are ignored; see below)
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

## Dependabot

Recorded 2026-08-17 against issue [#22](https://github.com/The-Allsparks/HELM/issues/22).

- **Gradle wrapper 8.7 → 9.x:** close. Gradle 9 is a major with breaking defaults; AMPER/MIMIC remain on 8.7. Java 11 source is unchanged, but a HELM-only Gradle 9 jump desyncs student setup from the rest of the org. Revisit only with a dated org-wide toolchain RFC.
- **JUnit BOM 5.10.x → 6.x:** close. Test-only, so FTC Android runtime is unaffected, but JUnit 6 is not the Allsparks test convention. Accept 5.x patch/minor when offered.
- **`actions/checkout` v4 → v7 and `actions/setup-java` v4 → v5:** wait. Do not merge floating major tags. A later PR may SHA-pin the current v4 line with version comments (F-DEP-002). Jumping majors without pins does not fix the supply-chain issue.
- Do not combine a major bump with feature work.

## Intentional HELM deviations

See [architecture.md](architecture.md). Modes plus phases; no REV observers; deterministic snapshot ids.
