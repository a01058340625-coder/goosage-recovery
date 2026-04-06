package com.goosage.domain.recovery;

public record RecoveryState(
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