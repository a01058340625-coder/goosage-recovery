package com.goosage.domain.study;

public class StudyState {
    private final int wrongReviews;
    private final int quizSubmits;
    private final int eventsCount;

    public StudyState(int wrongReviews, int quizSubmits, int eventsCount) {
        this.wrongReviews = wrongReviews;
        this.quizSubmits = quizSubmits;
        this.eventsCount = eventsCount;
    }

    public int getWrongReviews() { return wrongReviews; }
    public int getQuizSubmits() { return quizSubmits; }
    public int getEventsCount() { return eventsCount; }
}
