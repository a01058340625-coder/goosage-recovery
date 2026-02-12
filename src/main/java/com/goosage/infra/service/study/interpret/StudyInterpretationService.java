package com.goosage.infra.service.study.interpret;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.goosage.domain.study.StudySnapshot;
import com.goosage.domain.study.StudyState;
import com.goosage.infra.dao.StudyReadDao;
import com.goosage.infra.service.study.dto.StudyStateDto;

@Service
public class StudyInterpretationService {

    private final StudyReadDao studyReadDao;

    public StudyInterpretationService(StudyReadDao studyReadDao) {
        this.studyReadDao = studyReadDao;
    }

    // ✅ 표현/집계 DTO (기존 유지)
    public StudyStateDto getState(long userId) {
        LocalDate today = LocalDate.now();

        var opt = studyReadDao.findToday(userId);

        // ✅ 전체 기준 lastEventAt 확보 (오늘 이벤트 없어도 살아있게)
        var tsAll = studyReadDao.lastEventAtAll(userId);
        var lastEventAtAll = (tsAll != null) ? tsAll.toLocalDateTime() : null;

        int streakDays = studyReadDao.calcStreakDays(userId, today);

        if (opt.isEmpty()) {
            return new StudyStateDto(
                    today, false, streakDays,
                    0, 0, 0,
                    lastEventAtAll, null
            );
        }

        var row = opt.get();

        return new StudyStateDto(
                row.ymd(),
                row.eventsCount() > 0,
                streakDays,
                row.eventsCount(),
                row.quizSubmits(),
                row.wrongReviews(),
                // ✅ row.lastEventAt() 대신 "전체 마지막 이벤트"로 통일
                lastEventAtAll,
                null
        );
    }

    // ✅ 엔진 단일 진실: snapshot 기반으로 통일
    public StudyState getEngineState(long userId) {
        return getSnapshot(userId).state();
    }

    // ✅ 단일 출처 Snapshot (evidence 계산은 여기서만)
    public StudySnapshot getSnapshot(long userId) {

        StudyStateDto dto = getState(userId);

        StudyState state = new StudyState(
                dto.wrongReviews(),
                dto.quizSubmits(),
                dto.eventsCount()
        );

        int daysSinceLast = calcDaysSinceLastEvent(dto.lastEventAt());

        // TODO: 진짜 3일 집계로 교체 예정. 지금은 계약 유지용 브릿지.
        int recent3d = Math.max(0, dto.eventsCount());

        return new StudySnapshot(
                dto.ymd(),
                state,
                dto.isStudiedToday(),
                dto.streakDays(),
                dto.lastEventAt(),
                daysSinceLast,
                recent3d,
                dto.recentKnowledgeId()
        );
    }

    private int calcDaysSinceLastEvent(java.time.LocalDateTime lastEventAt) {
        if (lastEventAt == null) return 999;
        long days = java.time.Duration
                .between(lastEventAt, java.time.LocalDateTime.now())
                .toDays();
        return (int) Math.max(0, days);
    }
}
