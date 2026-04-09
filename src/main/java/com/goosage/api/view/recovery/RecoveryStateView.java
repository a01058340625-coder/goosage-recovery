package com.goosage.api.view.recovery;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class RecoveryStateView {

    private LocalDate ymd;
    private boolean studiedToday;
    private int streakDays;
    private int eventsCount;

    private int urgeLogs;
    private int betAttempts;
    private int betBlockedCount;
    private int recoveryActionCount;
    private int relapseSignalCount;

    private LocalDateTime lastEventAt;
    private Long recentKnowledgeId;

    public RecoveryStateView(
            LocalDate ymd,
            boolean studiedToday,
            int streakDays,
            int eventsCount,
            int urgeLogs,
            int betAttempts,
            int betBlockedCount,
            int recoveryActionCount,
            int relapseSignalCount,
            LocalDateTime lastEventAt,
            Long recentKnowledgeId
    ) {
        this.ymd = ymd;
        this.studiedToday = studiedToday;
        this.streakDays = streakDays;
        this.eventsCount = eventsCount;
        this.urgeLogs = urgeLogs;
        this.betAttempts = betAttempts;
        this.betBlockedCount = betBlockedCount;
        this.recoveryActionCount = recoveryActionCount;
        this.relapseSignalCount = relapseSignalCount;
        this.lastEventAt = lastEventAt;
        this.recentKnowledgeId = recentKnowledgeId;
    }

    public LocalDate getYmd() {
        return ymd;
    }

    public boolean isStudiedToday() {
        return studiedToday;
    }

    public int getStreakDays() {
        return streakDays;
    }

    public int getEventsCount() {
        return eventsCount;
    }

    public int getUrgeLogs() {
        return urgeLogs;
    }

    public int getBetAttempts() {
        return betAttempts;
    }

    public int getBetBlockedCount() {
        return betBlockedCount;
    }

    public int getRecoveryActionCount() {
        return recoveryActionCount;
    }

    public int getRelapseSignalCount() {
        return relapseSignalCount;
    }

    public LocalDateTime getLastEventAt() {
        return lastEventAt;
    }

    public Long getRecentKnowledgeId() {
        return recentKnowledgeId;
    }
}