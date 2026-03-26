package com.goosage.domain.study;

public record StudyState(
        int wrongReviews,
        int quizSubmits,
        int eventsCount,
        int wrongReviewDoneCount
) {

    public int justOpenCount() {
        int value = eventsCount - quizSubmits - wrongReviews - wrongReviewDoneCount;
        return Math.max(value, 0);
    }
}