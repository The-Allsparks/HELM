# Security Policy

## Supported versions

| Version | Supported |
| ------- | --------- |
| 0.1.x   | Yes       |

## Reporting a vulnerability

Please do **not** open a public issue for security problems that could put robots, students, or machines at risk.

Prefer:

1. GitHub Security Advisories for this repository (when available), or
2. A private email contact published by the maintainers

Include:

- A description of the issue
- Steps to reproduce
- Impact assessment (for example: unexpected motion, bypass of a lower-layer safe state, execution during replay, credential exposure)

## Safety expectations for this project

HELM intentionally:

- Never commands motors or servos
- Defaults to mode `OFF`
- Treats unknown, stale, and missing inputs as reasons to **block** tasks that require certainty
- Refuses physical output in replay
- Requires an operator disable path
- Must not construct active trees from untrusted runtime input

If you discover a path that issues hardware commands, enables execute modes automatically, or treats unknown safety state as available, treat it as a safety defect.

## Secrets

Never store passwords, Wi-Fi credentials, API keys, or tokens in the repository, issues, or exported logs.
