package com.goosage.app.recovery.interpret;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.goosage.domain.recovery.RecoveryReadPort;
import com.goosage.domain.recovery.RecoverySnapshot;
import com.goosage.domain.recovery.RecoveryState;

@Service
public class RecoveryInterpretationService {

    private final RecoveryReadPort recoveryReadPort;

    public RecoveryInterpretationService(RecoveryReadPort recoveryReadPort) {
        this.recoveryReadPort = recoveryReadPort;
    }

    public RecoveryState getEngineState(long userId) {
        return getSnapshot(userId).state();
    }

    public RecoverySnapshot getSnapshot(long userId) {

        LocalDate today = LocalDate.now();

        var opt = recoveryReadPort.findToday(userId, today);
        LocalDateTime lastEventAtAll = recoveryReadPort.lastEventAtAll(userId).orElse(null);

        int streakDays = recoveryReadPort.calcStreakDays(userId, today);

        int events = recoveryReadPort.todayEventCountFromEvents(userId, today);
        int urgeLogs = recoveryReadPort.todayUrgeLogFromEvents(userId, today);
        int betAttempts = recoveryReadPort.todayBetAttemptFromEvents(userId, today);
        int betBlockedCount = recoveryReadPort.todayBetBlockedFromEvents(userId, today);
        int recoveryActionCount = recoveryReadPort.todayRecoveryActionFromEvents(userId, today);
        int relapseSignalCount = recoveryReadPort.todayRelapseSignalFromEvents(userId, today);

        System.out.println("[INTERPRET-SVC] user=" + userId
                + " events=" + events
                + " urgeLogs=" + urgeLogs
                + " betAttempts=" + betAttempts
                + " betBlockedCount=" + betBlockedCount
                + " recoveryActionCount=" + recoveryActionCount
                + " relapseSignalCount=" + relapseSignalCount);

        Long recentKnowledgeId = null;

        RecoveryState state = new RecoveryState(
                urgeLogs,
                betAttempts,
                betBlockedCount,
                recoveryActionCount,
                relapseSignalCount,
                events
        );

        int daysSinceLast = calcDaysSinceLastEvent(lastEventAtAll);
        int recent3d = recoveryReadPort.recentEventCount3d(userId, today);

        return new RecoverySnapshot(
                today,
                state,
                events > 0,
                streakDays,
                lastEventAtAll,
                daysSinceLast,
                recent3d,
                recentKnowledgeId
        );
    }

    private int calcDaysSinceLastEvent(LocalDateTime lastEventAt) {
        if (lastEventAt == null) return 999;

        long days = java.time.Duration
                .between(lastEventAt, LocalDateTime.now())
                .toDays();

        return (int) Math.max(0, days);
    }
}