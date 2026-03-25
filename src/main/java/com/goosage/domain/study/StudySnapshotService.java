package com.goosage.domain.study;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class StudySnapshotService {

    private final StudyReadPort readPort;

    public StudySnapshotService(StudyReadPort readPort) {
        this.readPort = readPort;
    }

    public StudySnapshot snapshot(long userId, LocalDate nowDate, LocalDateTime nowDateTime) {

        var opt = readPort.findToday(userId, nowDate);

        // ✅ Port에서 LocalDateTime으로 받는다 (Timestamp 금지)
        LocalDateTime lastEventAtAll = readPort.lastEventAtAll(userId).orElse(null);

        int streakDays = readPort.calcStreakDays(userId, nowDate);

        int events = 0;
        int quiz = 0;
        int wrong = 0;
        int wrongDone = 0;
        Long recentKnowledgeId = null;
        boolean studiedToday = false;

        // 1) 기본: today 집계는 daily_learning 기반
        if (opt.isPresent()) {
            var a = opt.get();
            events = a.eventsCount();
            quiz = a.quizSubmits();
            wrong = a.wrongReviews();
            wrongDone = a.wrongReviewDoneCount();
            recentKnowledgeId = a.recentKnowledgeId(); // 지금은 null일 것
            studiedToday = events > 0; // (일단 유지, 아래에서 보정)
        }

        // 2) ✅ 보정: daily_learning이 0인데 사실(study_events)에 이벤트가 있으면 0 고정 장애 회피
        //    - 최소 변경 버전: eventsCount만 교차검증
        if (events == 0) {
            int e2 = readPort.todayEventCountFromEvents(userId, nowDate);
            if (e2 > 0) {
                events = e2;
                studiedToday = true; // 오늘 뭔가 했다는 뜻 (quiz 기준 재설계는 다음 단계)
            }
        }

        StudyState state = new StudyState(wrong, quiz, events, wrongDone);
        int daysSinceLast = calcDaysSinceLastEvent(lastEventAtAll, nowDateTime);

        // ✅ 진짜 3일 집계로 교체 완료
        int recent3d = readPort.recentEventCount3d(userId, nowDate);

        return new StudySnapshot(
                nowDate,
                state,
                studiedToday,
                streakDays,
                lastEventAtAll,
                daysSinceLast,
                recent3d,
                recentKnowledgeId
        );
    }

    private int calcDaysSinceLastEvent(LocalDateTime lastEventAt, LocalDateTime now) {
        if (lastEventAt == null) return 999;
        long days = Duration.between(lastEventAt, now).toDays();
        return (int) Math.max(0, days);
    }
}