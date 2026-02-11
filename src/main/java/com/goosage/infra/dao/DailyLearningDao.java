package com.goosage.infra.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DailyLearningDao {

    private final JdbcTemplate jdbcTemplate;

    public DailyLearningDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void upsertToday(long userId, boolean isQuizSubmit, boolean isWrongReviewDone) {
        int quizInc = isQuizSubmit ? 1 : 0;
        int wrongInc = isWrongReviewDone ? 1 : 0;

        String sql = """
            INSERT INTO daily_learning (user_id, ymd, events_count, quiz_submits, wrong_reviews, last_event_at)
            VALUES (?, CURDATE(), 1, ?, ?, NOW())
            ON DUPLICATE KEY UPDATE
              events_count   = events_count + 1,
              quiz_submits   = quiz_submits + VALUES(quiz_submits),
              wrong_reviews  = wrong_reviews + VALUES(wrong_reviews),
              last_event_at  = NOW()
            """;

        jdbcTemplate.update(sql, userId, quizInc, wrongInc);
    }
}
