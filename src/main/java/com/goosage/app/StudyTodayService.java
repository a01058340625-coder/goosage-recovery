package com.goosage.app;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.goosage.domain.study.StudyReadPort;
import com.goosage.domain.study.TodayRow;

@Service
public class StudyTodayService {

    private final StudyReadPort studyReadPort;

    public StudyTodayService(StudyReadPort studyReadPort) {
        this.studyReadPort = studyReadPort;
    }

    public StudyTodayResult getToday(long userId) {

        LocalDate today = LocalDate.now();
        var rowOpt = studyReadPort.findToday(userId, today);

        int events = studyReadPort.todayEventCountFromEvents(userId, today);
        int quiz = studyReadPort.todayQuizFromEvents(userId, today);
        int wrong = studyReadPort.todayWrongFromEvents(userId, today);

        if (events == 0) {
            return new StudyTodayResult(0, 0, 0, "오늘 아직 학습 기록이 없습니다");
        }

        TodayRow row = rowOpt.orElse(null);

        return new StudyTodayResult(
                events,
                quiz,
                wrong,
                messageFor(events, quiz)
        );
    }

    private String messageFor(int events, int quiz) {
        if (quiz > 0) return "오늘 퀴즈를 " + quiz + "회 진행했어요";
        if (events > 0) return "오늘 학습 활동이 있습니다";
        return "오늘 아직 학습 기록이 없습니다";
    }
}