package com.goosage.app;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.goosage.domain.recovery.RecoveryReadPort;
import com.goosage.domain.recovery.TodayRow;

@Service
public class RecoveryTodayService {

    private final RecoveryReadPort recoveryReadPort;

    public RecoveryTodayService(RecoveryReadPort recoveryReadPort) {
        this.recoveryReadPort = recoveryReadPort;
    }

    public RecoveryTodayResult getToday(long userId) {

        LocalDate today = LocalDate.now();
        var rowOpt = recoveryReadPort.findToday(userId, today);

        int events = recoveryReadPort.todayEventCountFromEvents(userId, today);
        int attempts = recoveryReadPort.todayBetAttemptFromEvents(userId, today);
        int risk = recoveryReadPort.todayRelapseSignalFromEvents(userId, today);

        if (events == 0) {
            return new RecoveryTodayResult(0, 0, 0, "오늘 아직 기록이 없습니다");
        }

        TodayRow row = rowOpt.orElse(null);

        if (row != null) {
            attempts = row.betAttempts();
            risk = row.relapseSignalCount();
        }

        return new RecoveryTodayResult(
                events,
                attempts,
                risk,
                messageFor(events, attempts, risk)
        );
    }

    private String messageFor(int events, int attempts, int risk) {
        if (risk > 0) return "오늘 위험 신호가 감지되었어요";
        if (attempts > 0) return "오늘 행동 시도가 " + attempts + "회 기록되었어요";
        if (events > 0) return "오늘 활동 기록이 있습니다";
        return "오늘 아직 기록이 없습니다";
    }
}