package com.goosage.domain.recovery;

import java.time.LocalDate;

public record RecoveryTodayRow(
        LocalDate ymd,
        int eventsCount,
        int quizSubmits,
        int wrongReviews
) {}
