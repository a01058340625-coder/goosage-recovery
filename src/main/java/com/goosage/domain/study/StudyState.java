package com.goosage.domain.study;

public record StudyState(
        int wrongReviews,
        int quizSubmits,
        int eventsCount,
        int wrongReviewDoneCount
) {

    /**
     * 현재 이벤트 모델이
     * JUST_OPEN / QUIZ_SUBMIT / REVIEW_WRONG / WRONG_REVIEW_DONE
     * 4종 기준이라는 전제에서 JUST_OPEN 개수를 역산한다.
     */
    public int justOpenCount() {
        int value = eventsCount - quizSubmits - wrongReviews - wrongReviewDoneCount;
        return Math.max(value, 0);
    }
}