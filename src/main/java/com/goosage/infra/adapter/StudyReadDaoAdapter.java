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
        // DAO가 내부에서 '오늘' 기준이면 today는 계약상만 받고 위임해도 OK
        return dao.findToday(userId)
                .map(r -> new TodayRow(
                        r.ymd(),
                        r.eventsCount(),
                        r.quizSubmits(),
                        r.wrongReviews(),
                        null // TODO: DAO row에 recentKnowledgeId 붙이면 여기 매핑
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
}
