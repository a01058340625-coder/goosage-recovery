package com.goosage.infra.dao;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StudyReadDao {

    private final JdbcTemplate jdbcTemplate;

    public StudyReadDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** 오늘 집계(단일 row) */
    public Optional<TodayRow> findToday(long userId) {

        String sql =
            "SELECT " +
            "  DATE(MAX(created_at)) AS ymd, " +
            "  COUNT(*) AS events_count, " +
            "  SUM(CASE WHEN event_type = 'QUIZ_SUBMIT' THEN 1 ELSE 0 END) AS quiz_submits, " +
            "  SUM(CASE WHEN event_type = 'WRONG_REVIEW_DONE' THEN 1 ELSE 0 END) AS wrong_reviews " +
            "FROM study_events " +
            "WHERE user_id = ? " +
            "  AND DATE(created_at) = CURDATE()";

        try {
            TodayRow row = jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> new TodayRow(
                    rs.getDate("ymd").toLocalDate(),
                    rs.getInt("events_count"),
                    rs.getInt("quiz_submits"),
                    rs.getInt("wrong_reviews")
                ),
                userId
            );

            // events_count가 0이면 “오늘 row 없음”으로 처리(정공법)
            if (row == null || row.eventsCount() <= 0) return Optional.empty();
            return Optional.of(row);

        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /** 전체 마지막 이벤트 시각 */
    public Timestamp lastEventAtAll(long userId) {
        String sql = "SELECT MAX(created_at) FROM study_events WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, Timestamp.class, userId);
    }

    public int calcStreakDays(long userId, LocalDate today) {

        String sql =
            "SELECT DISTINCT DATE(created_at) AS ymd " +
            "FROM study_events " +
            "WHERE user_id = ? " +
            "ORDER BY ymd DESC";

        List<LocalDate> days = jdbcTemplate.query(
            sql,
            (rs, rowNum) -> rs.getDate("ymd").toLocalDate(),
            userId
        );

        if (days.isEmpty()) return 0;

        // 기준 anchor:
        // - 오늘 학습했으면 today부터
        // - 아니면 yesterday부터 (오늘 비어있어도 streak 유지되게)
        LocalDate anchor = today;
        if (!days.contains(today)) {
            anchor = today.minusDays(1);
        }

        int streak = 0;
        LocalDate cursor = anchor;

        // days는 내림차순. cursor부터 하루씩 감소하며 존재 여부 체크
        // (데이터량 적을 때는 contains로 충분. 커지면 Set으로 바꾸면 됨)
        while (days.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }

        return streak;
    }
}
