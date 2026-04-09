package com.goosage.infra.adapter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.goosage.domain.recovery.RecoveryReadPort;
import com.goosage.domain.recovery.TodayRow;
import com.goosage.infra.dao.RecoveryReadDao;

@Component
public class RecoveryReadDaoAdapter implements RecoveryReadPort {

    private final RecoveryReadDao dao;

    public RecoveryReadDaoAdapter(RecoveryReadDao dao) {
        this.dao = dao;
    }

    @Override
    public Optional<TodayRow> findToday(long userId, LocalDate today) {
        return dao.findToday(userId)
                .map(r -> new TodayRow(
                        r.ymd(),
                        r.eventsCount(),
                        r.urgeLogs(),
                        r.betAttempts(),
                        r.betBlockedCount(),
                        r.recoveryActionCount(),
                        r.relapseSignalCount()
                ));
    }

    @Override
    public Optional<LocalDateTime> lastEventAtAll(long userId) {
        return dao.lastEventAtAll(userId);
    }

    @Override
    public int recentEventCount3d(long userId, LocalDate today) {
        return dao.recentEventCount3d(userId, today);
    }

    @Override
    public int calcStreakDays(long userId, LocalDate today) {
        return dao.calcStreakDays(userId, today);
    }

    @Override
    public int todayEventCountFromEvents(long userId, LocalDate today) {
        return dao.todayEventCountFromEvents(userId, today);
    }

    @Override
    public int recentUrgeLog3d(long userId, LocalDate today) {
        return dao.recentUrgeLog3d(userId, today);
    }

    @Override
    public int recentBetAttempt3d(long userId, LocalDate today) {
        return dao.recentBetAttempt3d(userId, today);
    }

    @Override
    public int recentBetBlocked3d(long userId, LocalDate today) {
        return dao.recentBetBlocked3d(userId, today);
    }

    @Override
    public int recentRecoveryAction3d(long userId, LocalDate today) {
        return dao.recentRecoveryAction3d(userId, today);
    }

    @Override
    public int recentRelapseSignal3d(long userId, LocalDate today) {
        return dao.recentRelapseSignal3d(userId, today);
    }

    @Override
    public int todayUrgeLogFromEvents(long userId, LocalDate today) {
        return dao.todayUrgeLogFromEvents(userId, today);
    }

    @Override
    public int todayBetAttemptFromEvents(long userId, LocalDate today) {
        return dao.todayBetAttemptFromEvents(userId, today);
    }

    @Override
    public int todayBetBlockedFromEvents(long userId, LocalDate today) {
        return dao.todayBetBlockedFromEvents(userId, today);
    }

    @Override
    public int todayRecoveryActionFromEvents(long userId, LocalDate today) {
        return dao.todayRecoveryActionFromEvents(userId, today);
    }

    @Override
    public int todayRelapseSignalFromEvents(long userId, LocalDate today) {
        return dao.todayRelapseSignalFromEvents(userId, today);
    }
}