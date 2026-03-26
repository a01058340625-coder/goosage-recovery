package com.goosage.infra.adapter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.goosage.domain.study.StudyReadPort;
import com.goosage.domain.study.TodayRow;
import com.goosage.infra.dao.StudyReadDao;

@Component
public class StudyReadDaoAdapter implements StudyReadPort {

    private final StudyReadDao dao;

    public StudyReadDaoAdapter(StudyReadDao dao) {
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
        			    r.wrongReviewDoneCount(),   // 🔥 추가
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
    
    @Override
    public int recentWrong3d(long userId, LocalDate today) {
        return dao.recentWrong3d(userId, today);
    }

    @Override
    public int recentWrongDone3d(long userId, LocalDate today) {
        return dao.recentWrongDone3d(userId, today);
    }
    
    @Override
    public int todayWrongFromEvents(long userId, LocalDate today) {
        return dao.todayWrongFromEvents(userId, today);
    }

    @Override
    public int todayWrongDoneFromEvents(long userId, LocalDate today) {
        return dao.todayWrongDoneFromEvents(userId, today);
    }
    
    @Override
    public int todayQuizFromEvents(long userId, LocalDate today) {
        return dao.todayQuizFromEvents(userId, today);
    }
}