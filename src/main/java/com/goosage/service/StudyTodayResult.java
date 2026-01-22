package com.goosage.service;

public record StudyTodayResult(
        int eventsCount,
        int quizSubmits,
        int wrongReviews,
        String message
) {}
