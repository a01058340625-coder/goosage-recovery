package com.goosage.app;

public record RecoveryTodayResult(
        int eventsCount,
        int betAttempts,
        int relapseSignalCount,
        String message
) {}