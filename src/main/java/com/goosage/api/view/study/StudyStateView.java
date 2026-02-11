package com.goosage.api.view.study;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StudyStateView {

    private LocalDate ymd;
    private boolean studiedToday;
    private int streakDays;
    private int eventsCount;
    private int quizSubmits;
    private int wrongReviews;
    private LocalDateTime lastEventAt;
    private Long recentKnowledgeId;

    public StudyStateView(
            LocalDate ymd,
            boolean studiedToday,
            int streakDays,
            int eventsCount,
            int quizSubmits,
            int wrongReviews,
            LocalDateTime lastEventAt,
            Long recentKnowledgeId
    ) {
        this.ymd = ymd;
        this.studiedToday = studiedToday;
        this.streakDays = streakDays;
        this.eventsCount = eventsCount;
        this.quizSubmits = quizSubmits;
        this.wrongReviews = wrongReviews;
        this.lastEventAt = lastEventAt;
        this.recentKnowledgeId = recentKnowledgeId;
    }

    public LocalDate getYmd() { return ymd; }
    public boolean isStudiedToday() { return studiedToday; }
    public int getStreakDays() { return streakDays; }
    public int getEventsCount() { return eventsCount; }
    public int getQuizSubmits() { return quizSubmits; }
    public int getWrongReviews() { return wrongReviews; }
    public LocalDateTime getLastEventAt() { return lastEventAt; }
    public Long getRecentKnowledgeId() { return recentKnowledgeId; }
}
