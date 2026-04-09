package com.goosage.infra.dao;

import java.time.LocalDate;

public record TodayRowRecord(
        LocalDate ymd,
        int eventsCount,
        int urgeLogs,
        int betAttempts,
        int betBlockedCount,
        int recoveryActionCount,
        int relapseSignalCount
) {
}