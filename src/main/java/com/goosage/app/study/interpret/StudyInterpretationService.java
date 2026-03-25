package com.goosage.app.study.interpret;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.goosage.domain.study.StudyReadPort;
import com.goosage.domain.study.StudySnapshot;
import com.goosage.domain.study.StudyState;

@Service
public class StudyInterpretationService {

    private final StudyReadPort studyReadPort;

    public StudyInterpretationService(StudyReadPort studyReadPort) {
        this.studyReadPort = studyReadPort;
    }

    // ✅ 엔진 단일 진실: snapshot 기반
    public StudyState getEngineState(long userId) {
        return getSnapshot(userId).state();
    }

    public StudySnapshot getSnapshot(long userId) {

        LocalDate today = LocalDate.now();

        var opt = studyReadPort.findToday(userId, today);

        LocalDateTime lastEventAtAll = studyReadPort.lastEventAtAll(userId).orElse(null);

        int streakDays = studyReadPort.calcStreakDays(userId, today);
        int events = 0;
        int quiz = 0;
        int wrong = 0;
        int wrongDone = 0;
        Long recentKnowledgeId = null;

        if (opt.isPresent()) {
            var row = opt.get();
            events = row.eventsCount();
            quiz = row.quizSubmits();
            wrong = studyReadPort.recentWrong3d(userId, today);
            wrongDone = studyReadPort.recentWrongDone3d(userId, today);
        }

        StudyState state = new StudyState(wrong, quiz, events, wrongDone);
        
        int daysSinceLast = calcDaysSinceLastEvent(lastEventAtAll);

        int recent3d = studyReadPort.recentEventCount3d(userId, today);

        return new StudySnapshot(
                today,
                state,
                events > 0,
                streakDays,
                lastEventAtAll,
                daysSinceLast,
                recent3d,
                recentKnowledgeId
        );
    }

    private int calcDaysSinceLastEvent(LocalDateTime lastEventAt) {
        if (lastEventAt == null) return 999;

        long days = java.time.Duration
                .between(lastEventAt, LocalDateTime.now())
                .toDays();

        return (int) Math.max(0, days);
    }
}