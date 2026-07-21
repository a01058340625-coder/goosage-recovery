package com.goosage.domain.recovery.message;

public record RecoveryMessageSignal(
        int urgeLogDelta,
        int betAttemptDelta,
        int betBlockedDelta,
        int recoveryActionDelta,
        int relapseSignalDelta,
        double confidence,
        String reason
) {
}