package com.goosage.infra.dao;

import java.time.LocalDate;

/**
 * DAO 전용 Row (JDBC 결과)
 */
public record TodayRow(
        LocalDate ymd,
        int eventsCount,
        int quizSubmits,
        int wrongReviews
) {}
