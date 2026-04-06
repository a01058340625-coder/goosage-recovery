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
                        r.quizSubmits(),
                        r.wrongReviews(),
                        r.wrongReviewDoneCount(),
                        null
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

    // 🔥 이름만 바꿔서 매핑
    @Override
    public int recentRiskSignal3d(long userId, LocalDate today) {
        return dao.recentWrong3d(userId, today);
    }

    @Override
    public int recentRecoveryAction3d(long userId, LocalDate today) {
        return dao.recentWrongDone3d(userId, today);
    }

    @Override
    public int todayRiskSignalFromEvents(long userId, LocalDate today) {
        return dao.todayWrongFromEvents(userId, today);
    }

    @Override
    public int todayRecoveryActionFromEvents(long userId, LocalDate today) {
        return dao.todayWrongDoneFromEvents(userId, today);
    }

    @Override
    public int todayActionFromEvents(long userId, LocalDate today) {
        return dao.todayQuizFromEvents(userId, today);
    }
}