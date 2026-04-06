package com.goosage.app;

public record RecoveryTodayResult(
        int eventsCount,
        int quizSubmits,
        int wrongReviews,
        String message
) {}
