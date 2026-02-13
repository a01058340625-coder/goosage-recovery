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
        LocalDateTime lastEventAtAll = readPort.lastEventAtAll(userId);

        int streakDays = readPort.calcStreakDays(userId, nowDate);

        int events = 0, quiz = 0, wrong = 0;
        Long recentKnowledgeId = null;
        boolean studiedToday = false;

        if (opt.isPresent()) {
            var a = opt.get();
            events = a.eventsCount();
            quiz = a.quizSubmits();
            wrong = a.wrongReviews();
            recentKnowledgeId = a.recentKnowledgeId(); // 지금은 null일 것
            studiedToday = events > 0;
        }

        StudyState state = new StudyState(wrong, quiz, events);
        int daysSinceLast = calcDaysSinceLastEvent(lastEventAtAll, nowDateTime);

        // TODO: recent3d 진짜 집계로 교체 예정
        int recent3d = Math.max(0, events);

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
