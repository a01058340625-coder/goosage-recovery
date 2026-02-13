package com.goosage.infra.service.study.interpret;

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

    // ✅ 단일 출처 Snapshot
    public StudySnapshot getSnapshot(long userId) {

        LocalDate today = LocalDate.now();

        var opt = studyReadPort.findToday(userId, today);

        LocalDateTime lastEventAtAll = studyReadPort.lastEventAtAll(userId);

        int streakDays = studyReadPort.calcStreakDays(userId, today);

        int events = 0;
        int quiz = 0;
        int wrong = 0;
        Long recentKnowledgeId = null; // 아직 TodayRow에 없으니 null 고정

        if (opt.isPresent()) {
            var row = opt.get();
            events = row.eventsCount();
            quiz = row.quizSubmits();
            wrong = row.wrongReviews();
        }

        StudyState state = new StudyState(wrong, quiz, events);
        int daysSinceLast = calcDaysSinceLastEvent(lastEventAtAll);

        // TODO: 진짜 3일 집계로 교체 예정. 지금은 계약 유지용 브릿지.
        int recent3d = Math.max(0, events);

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
