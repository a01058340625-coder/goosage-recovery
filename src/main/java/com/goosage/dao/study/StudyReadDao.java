package com.goosage.dao.study;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StudyReadDao {

    private final JdbcTemplate jdbcTemplate;

    public StudyReadDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<TodayRow> findToday(long userId) {

        // ✅ 핵심: ymd 컬럼이 없으니 DATE(created_at)로 오늘을 잡는다
        // ✅ 핵심: type이 아니라 event_type 사용
        String sql = """
            SELECT
                DATE(MAX(created_at)) AS ymd,
                COUNT(*) AS events_count,
                SUM(CASE WHEN event_type = 'QUIZ_SUBMIT' THEN 1 ELSE 0 END) AS quiz_submits,
                SUM(CASE WHEN event_type = 'WRONG_REVIEW_DONE' THEN 1 ELSE 0 END) AS wrong_reviews,
                MAX(created_at) AS last_event_at
            FROM study_events
            WHERE user_id = ?
              AND DATE(created_at) = CURDATE()
        """;

        try {
            TodayRow row = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                LocalDate ymd = rs.getDate("ymd") != null ? rs.getDate("ymd").toLocalDate() : LocalDate.now();
                int eventsCount = rs.getInt("events_count");
                int quizSubmits = rs.getInt("quiz_submits");
                int wrongReviews = rs.getInt("wrong_reviews");

                Timestamp ts = rs.getTimestamp("last_event_at");
                LocalDateTime lastEventAt = (ts != null) ? ts.toLocalDateTime() : null;

                // 오늘 아무 이벤트도 없으면 COUNT(*) = 0 이고, 그때는 "없다"로 처리하는 게 깔끔
                if (eventsCount <= 0) return null;

                return new TodayRow(ymd, eventsCount, quizSubmits, wrongReviews, lastEventAt);
            }, userId);

            return Optional.ofNullable(row);

        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
