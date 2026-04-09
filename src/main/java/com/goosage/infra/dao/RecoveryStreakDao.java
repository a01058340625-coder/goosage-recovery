package com.goosage.infra.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RecoveryStreakDao {

    private final JdbcTemplate jdbcTemplate;

    public RecoveryStreakDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int countStreak(long userId) {
        String sql = """
            SELECT COUNT(*)
            FROM recovery_daily_learning
            WHERE user_id = ?
              AND ymd <= CURDATE()
              AND (
                SELECT COUNT(*)
                FROM recovery_daily_learning d2
                WHERE d2.user_id = ?
                  AND d2.ymd BETWEEN recovery_daily_learning.ymd AND CURDATE()
              ) = DATEDIFF(CURDATE(), recovery_daily_learning.ymd) + 1
        """;

        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, userId, userId);
        return result != null ? result : 0;
    }
}
