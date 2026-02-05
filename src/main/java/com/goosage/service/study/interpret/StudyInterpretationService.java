package com.goosage.service.study.interpret;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.goosage.dao.study.StudyReadDao;
import com.goosage.service.study.dto.StudyStateDto;


@Service
public class StudyInterpretationService {

    private final StudyReadDao studyReadDao;

    public StudyInterpretationService(StudyReadDao studyReadDao) {
        this.studyReadDao = studyReadDao;
    }

    public StudyStateDto getState(Long userId) {
        LocalDate today = LocalDate.now();

        var opt = studyReadDao.findToday(userId);

        if (opt.isEmpty()) {
            return new StudyStateDto(
                today, false, 0,
                0, 0, 0,
                null, null
            );
        }

        var row = opt.get();

        int streakDays = studyReadDao.calcStreakDays(userId, today);

        return new StudyStateDto(
            row.ymd(),
            row.eventsCount() > 0,
            streakDays,            // ✅ 여기만 채움
            row.eventsCount(),
            row.quizSubmits(),
            row.wrongReviews(),
            row.lastEventAt(),
            null
        );
    }
}