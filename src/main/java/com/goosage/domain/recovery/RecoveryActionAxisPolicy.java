package com.goosage.domain.recovery;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.NextActionType;

@Component
public class RecoveryActionAxisPolicy {

    public boolean matches(NextActionType actionType, RecoveryStateAxes axes) {
        if (actionType == null || axes == null) {
            return false;
        }

        return switch (actionType) {
            case PROCESS_RISK_SIGNAL -> matchesProcessRiskSignal(axes);
            case RECOVERY_CHECK -> matchesRecoveryCheck(axes);
            case DO_RECOVERY_ACTION -> matchesDoRecoveryAction(axes);
            case MINIMUM_CONTACT -> matchesMinimumContact(axes);
            case TODAY_SAFE -> matchesTodaySafe(axes);
            default -> false;
        };
    }

    public Map<String, Object> explain(NextActionType actionType, RecoveryStateAxes axes) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("actionType", actionType != null ? actionType.name() : null);

        if (axes == null) {
            result.put("matched", false);
            result.put("reason", "axes is null");
            return result;
        }

        result.put("risk", round(axes.risk()));
        result.put("defense", round(axes.defense()));
        result.put("recovery", round(axes.recovery()));
        result.put("instability", round(axes.instability()));
        result.put("continuity", round(axes.continuity()));

        boolean matched = matches(actionType, axes);
        result.put("matched", matched);
        result.put("reason", buildReason(actionType, axes));

        return result;
    }

    private boolean matchesProcessRiskSignal(RecoveryStateAxes axes) {
        return axes.risk() >= 0.55
                || (axes.instability() >= 0.50 && axes.risk() >= 0.40);
    }

    private boolean matchesRecoveryCheck(RecoveryStateAxes axes) {
        return axes.recovery() >= 0.60
                && axes.risk() < 0.45
                && axes.continuity() >= 0.40;
    }

    private boolean matchesDoRecoveryAction(RecoveryStateAxes axes) {
        return axes.risk() >= 0.35
                && axes.recovery() < 0.60
                && axes.defense() < 0.50;
    }

    private boolean matchesMinimumContact(RecoveryStateAxes axes) {
        return axes.continuity() < 0.35
                && axes.risk() < 0.55;
    }

    private boolean matchesTodaySafe(RecoveryStateAxes axes) {
        return axes.recovery() >= 0.65
                && axes.risk() < 0.30
                && axes.instability() < 0.35
                && axes.continuity() >= 0.55;
    }

    private String buildReason(NextActionType actionType, RecoveryStateAxes axes) {
        if (actionType == null) {
            return "actionType is null";
        }

        return switch (actionType) {
            case PROCESS_RISK_SIGNAL ->
                    "risk>=0.55 or (instability>=0.50 and risk>=0.40)";
            case RECOVERY_CHECK ->
                    "recovery>=0.60 and risk<0.45 and continuity>=0.40";
            case DO_RECOVERY_ACTION ->
                    "risk>=0.35 and recovery<0.60 and defense<0.50";
            case MINIMUM_CONTACT ->
                    "continuity<0.35 and risk<0.55";
            case TODAY_SAFE ->
                    "recovery>=0.65 and risk<0.30 and instability<0.35 and continuity>=0.55";
            default ->
                    "unsupported actionType";
        };
    }

    private double round(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }
}