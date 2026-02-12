package com.goosage.domain.study;

public record StudyState(
    int wrongReviews,
    int quizSubmits,
    int eventsCount
) {}
