package com.goosage.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.goosage.dao.study.StudyReadDao;
import com.goosage.dao.study.TodayRow;

@Service
public class StudyTodayService {

    private final StudyReadDao studyReadDao;

    public StudyTodayService(StudyReadDao studyReadDao) {
        this.studyReadDao = studyReadDao;
    }

    public StudyTodayResult getToday(long userId) {
        Optional<TodayRow> rowOpt = studyReadDao.findToday(userId);

        if (rowOpt.isEmpty()) {
            return new StudyTodayResult(
                    0,
                    0,
                    0,
                    "오늘 아직 학습 기록이 없습니다"
            );
        }

        TodayRow row = rowOpt.get();

        return new StudyTodayResult(
                row.eventsCount(),
                row.quizSubmits(),
                row.wrongReviews(),
                messageFor(row)
        );
    }

    private String messageFor(TodayRow row) {
        if (row.quizSubmits() > 0) {
            return "오늘 퀴즈를 " + row.quizSubmits() + "회 진행했어요";
        }
        if (row.eventsCount() > 0) {
            return "오늘 학습 활동이 있습니다";
        }
        return "오늘 아직 학습 기록이 없습니다";
    }
}
