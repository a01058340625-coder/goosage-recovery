package com.goosage.dao.study;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TodayRow(
        LocalDate ymd,
        int eventsCount,
        int quizSubmits,
        int wrongReviews,
        LocalDateTime lastEventAt
) {}
