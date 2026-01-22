package com.goosage.dao.study;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StudyReadDao {

    private final JdbcTemplate jdbcTemplate;

    public StudyReadDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<TodayRow> findToday(long userId) {
        String sql = """
            SELECT ymd, events_count, quiz_submits, wrong_reviews, last_event_at
            FROM daily_learning
            WHERE user_id = ?
              AND ymd = CURDATE()
            LIMIT 1
        """;

        List<TodayRow> rows = jdbcTemplate.query(
            sql,
            (rs, n) -> new TodayRow(
                rs.getDate("ymd").toLocalDate(),
                rs.getInt("events_count"),
                rs.getInt("quiz_submits"),
                rs.getInt("wrong_reviews"),
                rs.getTimestamp("last_event_at") != null
                    ? rs.getTimestamp("last_event_at").toLocalDateTime()
                    : null
            ),
            userId
        );

        return rows.stream().findFirst();
    }
}
