package com.goosage.domain.study;

public interface DailyLearningPort {
    void upsertToday(long userId, boolean isQuizSubmit, boolean isReviewWrong);
}