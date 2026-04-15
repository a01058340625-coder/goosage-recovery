package com.goosage.domain.recovery;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

@Service
public class RecoverySnapshotService {

    private final RecoveryReadPort readPort;

    public RecoverySnapshotService(RecoveryReadPort readPort) {
        this.readPort = readPort;
    }

    public RecoverySnapshot snapshot(long userId, LocalDate nowDate, LocalDateTime nowDateTime) {

        readPort.findToday(userId, nowDate);

        LocalDateTime lastEventAtAll = readPort.lastEventAtAll(userId).orElse(null);
        int streakDays = readPort.calcStreakDays(userId, nowDate);

        int events = readPort.todayEventCountFromEvents(userId, nowDate);
        int urgeLogs = readPort.todayUrgeLogFromEvents(userId, nowDate);
        int betAttempts = readPort.todayBetAttemptFromEvents(userId, nowDate);
        int betBlockedCount = readPort.todayBetBlockedFromEvents(userId, nowDate);
        int recoveryActionCount = readPort.todayRecoveryActionFromEvents(userId, nowDate);
        int relapseSignalCount = readPort.todayRelapseSignalFromEvents(userId, nowDate);

        int recent3d = readPort.recentEventCount3d(userId, nowDate);
        int daysSinceLast = calcDaysSinceLastEvent(lastEventAtAll, nowDateTime);

        // day29 상태기반 보정
        if (events == 0 && recent3d > 0) {
            events = 1;
        }

        if (recoveryActionCount == 0 && recent3d > 0) {
            recoveryActionCount = 1;
        }

        Long recentKnowledgeId = null;
        boolean studiedToday = events > 0;

        RecoveryState state = new RecoveryState(
                urgeLogs,
                betAttempts,
                betBlockedCount,
                recoveryActionCount,
                relapseSignalCount,
                events
        );

        System.out.println("[SNAPSHOT-SVC] user=" + userId
                + " events=" + events
                + " urgeLogs=" + urgeLogs
                + " betAttempts=" + betAttempts
                + " betBlockedCount=" + betBlockedCount
                + " recoveryActionCount=" + recoveryActionCount
                + " relapseSignalCount=" + relapseSignalCount
                + " recent3d=" + recent3d
                + " streakDays=" + streakDays
                + " daysSinceLast=" + daysSinceLast
                + " lastEventAtAll=" + lastEventAtAll);

        return new RecoverySnapshot(
                nowDate,
                state,
                studiedToday,
                streakDays,
                lastEventAtAll,
                daysSinceLast,
                recent3d,
                recentKnowledgeId
        );
    }

    private int calcDaysSinceLastEvent(LocalDateTime lastEventAt, LocalDateTime now) {
        if (lastEventAt == null) return 999;
        long days = Duration.between(lastEventAt, now).toDays();
        return (int) Math.max(0, days);
    }
}