package com.goosage.domain.study;

import java.time.LocalDate;

public record StudyTodayRow(
        LocalDate ymd,
        int eventsCount,
        int quizSubmits,
        int wrongReviews
) {}
