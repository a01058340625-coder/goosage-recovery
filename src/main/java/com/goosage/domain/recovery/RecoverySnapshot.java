package com.goosage.domain.recovery;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * RecoverySnapshot = SSOT(단일 진실)
 * - Coach / Prediction / NextAction이 참고하는 읽기 전용 스냅샷
 * - Rule / Engine / Controller는 DB를 직접 보지 않고 Snapshot만 본다.
 */
public record RecoverySnapshot(
        LocalDate ymd,
        RecoveryState state,
        boolean studiedToday,
        int streakDays,
        LocalDateTime lastEventAt,
        int daysSinceLastEvent,
        int recentEventCount3d,
        Long recentKnowledgeId
) {

    public double urgeRatio() {
        if (state == null || state.eventsCount() <= 0) return 0.0;
        return (double) state.urgeLogs() / state.eventsCount();
    }

    public double attemptRatio() {
        if (state == null || state.eventsCount() <= 0) return 0.0;
        return (double) state.betAttempts() / state.eventsCount();
    }

    public double blockedRatio() {
        if (state == null || state.eventsCount() <= 0) return 0.0;
        return (double) state.betBlockedCount() / state.eventsCount();
    }

    public double recoveryRatio() {
        if (state == null || state.eventsCount() <= 0) return 0.0;
        return (double) state.recoveryActionCount() / state.eventsCount();
    }

    public double relapseRatio() {
        if (state == null || state.eventsCount() <= 0) return 0.0;
        return (double) state.relapseSignalCount() / state.eventsCount();
    }

    public boolean hasUrge() {
        return state != null && state.urgeLogs() > 0;
    }

    public boolean hasAttempt() {
        return state != null && state.betAttempts() > 0;
    }

    public boolean hasBlocked() {
        return state != null && state.betBlockedCount() > 0;
    }

    public boolean hasRecoveryProgress() {
        return state != null && state.recoveryActionCount() > 0;
    }

    public boolean hasRelapseSignal() {
        return state != null && state.relapseSignalCount() > 0;
    }

    public boolean isRecoverySafe() {
        return state != null
                && state.recoveryActionCount() > 0
                && state.recoveryActionCount() > (state.betAttempts() + state.relapseSignalCount());
    }
}