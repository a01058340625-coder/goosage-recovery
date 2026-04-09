package com.goosage.domain.recovery;

public record RecoveryState(
        int urgeLogs,
        int betAttempts,
        int betBlockedCount,
        int recoveryActionCount,
        int relapseSignalCount,
        int eventsCount
) {
}