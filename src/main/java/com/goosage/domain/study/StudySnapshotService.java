package com.goosage.domain.study;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

@Service
public class StudySnapshotService {

    private final StudyReadPort readPort;

    public StudySnapshotService(StudyReadPort readPort) {
        this.readPort = readPort;
    }

    public StudySnapshot snapshot(long userId, LocalDate nowDate, LocalDateTime nowDateTime) {

        var opt = readPort.findToday(userId, nowDate);

        LocalDateTime lastEventAtAll = readPort.lastEventAtAll(userId).orElse(null);
        int streakDays = readPort.calcStreakDays(userId, nowDate);

        int events = readPort.todayEventCountFromEvents(userId, nowDate);
        int quiz = readPort.todayQuizFromEvents(userId, nowDate);
        int wrong = readPort.todayWrongFromEvents(userId, nowDate);
        int wrongDone = readPort.todayWrongDoneFromEvents(userId, nowDate);

        // 👇 여기 추가
        System.out.println("[SNAPSHOT-SVC] user=" + userId
        	    + " events=" + events
        	    + " quiz=" + quiz
        	    + " wrong=" + wrong
        	    + " wrongDone=" + wrongDone);
        
        Long recentKnowledgeId = null;
        boolean studiedToday = events > 0;
        if (opt.isPresent()) {
            var a = opt.get();
            recentKnowledgeId = a.recentKnowledgeId();
        }

        StudyState state = new StudyState(wrong, quiz, events, wrongDone);
        int daysSinceLast = calcDaysSinceLastEvent(lastEventAtAll, nowDateTime);
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