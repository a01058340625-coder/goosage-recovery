package com.goosage.infra.dao;

import java.time.LocalDate;

public record TodayRowRecord(
        LocalDate ymd,
        int eventsCount,
        int quizSubmits,
        int wrongReviews,
        int wrongReviewDoneCount
) {}