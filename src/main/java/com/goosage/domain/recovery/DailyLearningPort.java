package com.goosage.domain.recovery;

public interface DailyLearningPort {
    void upsertToday(long userId, boolean isQuizSubmit, boolean isReviewWrong);
}