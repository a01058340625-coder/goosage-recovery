package com.goosage.infra.service.study.interpret;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.goosage.infra.dao.StudyReadDao;
import com.goosage.infra.service.study.dto.StudyStateDto;


@Service
public class StudyInterpretationService {

    private final StudyReadDao studyReadDao;

    public StudyInterpretationService(StudyReadDao studyReadDao) {
        this.studyReadDao = studyReadDao;
    }

    public StudyStateDto getState(Long userId) {
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
}