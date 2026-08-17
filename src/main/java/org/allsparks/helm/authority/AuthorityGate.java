package org.allsparks.helm.authority;

import org.allsparks.helm.HelmConfig;
import org.allsparks.helm.HelmMode;

/**
 * Execution-authority gate. Physical output is refused in this scaffold even if
 * a caller sets an execute mode or feature flag.
 */
public final class AuthorityGate {
    private final HelmConfig config;

    public AuthorityGate(HelmConfig config) {
        this.config = config;
    }

    public boolean allowsPhysicalOutput() {
        return false;
    }

    public boolean allowsSimulatedTick() {
        return !config.operatorDisable()
                && config.mode() != HelmMode.OFF
                && !config.mode().requestsExecutionAuthority();
    }

    public String denialExplanation() {
        if (config.operatorDisable()) {
            return "Operator disable path is active";
        }
        if (config.mode() == HelmMode.OFF) {
            return "HELM mode is OFF";
        }
        if (config.mode().isReplay()) {
            return "Replay must never create physical outputs";
        }
        if (config.mode().requestsExecutionAuthority()) {
            return "Phase 3+ execution is not approved; HELM cannot command hardware";
        }
        if (config.flags().isAnyExecutionEnabled()) {
            return "Execution flags are set but the Phase 3/5 approval gates are closed";
        }
        if (config.flags().isRequireTraceForAuthority() && !config.validatedTraceRecording()) {
            return "Active HELM authority requires validated TRACE recording";
        }
        return "HELM does not have execution authority in this scaffold";
    }
}
