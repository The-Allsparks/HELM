# Safety

HELM must:

- Never directly command motors or servos
- Respect lower-layer safety restrictions
- Support immediate cancellation (simulated walker; live executor not approved)
- Use bounded retries and explicit timeouts
- Require an explicit safe-terminal mark (`IntentNode.safeTerminal(...)`) in last-child fallback/recovery position on validated trees. Action names are not interpreted. Phase 2 validation is not a substitute for MIMIC, BEACON, or hardware interlocks.
- Prevent conflicting exclusive resource claims in parallel nodes
- Detect stale inputs
- Preserve an operator disable path
- Avoid unexpected objective switching (`CommitmentPolicy` for later selection)
- Report every fallback and record preemption as its own status
- Fail safely when an adapter throws or disappears (`UNAVAILABLE`)
- Prevent physical output during replay
- Avoid constructing active trees from untrusted runtime input
- Validate externally defined trees before execution
- Avoid arbitrary code execution from behavior definitions (trees are data)
- Never continue merely because a task was previously running

When safety state is unknown, HELM must not assume a capability is available.
