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
        int actions = recoveryReadPort.todayActionFromEvents(userId, today);
        int risk = recoveryReadPort.todayRiskSignalFromEvents(userId, today);
        
        if (events == 0) {
            return new RecoveryTodayResult(0, 0, 0, "오늘 아직 기록이 없습니다");
        }

        TodayRow row = rowOpt.orElse(null);

        return new RecoveryTodayResult(
                events,
                actions,
                risk,
                messageFor(events, actions)
        );
    }

    private String messageFor(int events, int actions) {
        if (actions > 0) return "오늘 행동을 " + actions + "회 기록했어요";
        if (events > 0) return "오늘 활동 기록이 있습니다";
        return "오늘 아직 기록이 없습니다";
    }
}