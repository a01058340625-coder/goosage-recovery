package com.goosage.app;

public record StudyTodayResult(
        int eventsCount,
        int quizSubmits,
        int wrongReviews,
        String message
) {}
