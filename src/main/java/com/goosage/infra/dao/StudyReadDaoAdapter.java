package com.goosage.infra.dao;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.goosage.domain.study.StudyReadPort;
import com.goosage.domain.study.StudyTodayRow;

@Repository
public class StudyReadDaoAdapter implements StudyReadPort {

    private final StudyReadDao studyReadDao;

    public StudyReadDaoAdapter(StudyReadDao studyReadDao) {
        this.studyReadDao = studyReadDao;
    }

    @Override
    public Optional<StudyTodayRow> findToday(long userId) {
        return studyReadDao.findToday(userId)
                .map(r -> new StudyTodayRow(
                        r.ymd(),
                        r.eventsCount(),
                        r.quizSubmits(),
                        r.wrongReviews()
                ));
    }
}
