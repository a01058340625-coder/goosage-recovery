package com.goosage.infra.dao;

import java.sql.Timestamp;
import java.time.LocalDate;
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

    /** streakDays 계산(네 기존 DAO 로직 있으면 그걸로 교체) */
    public int calcStreakDays(long userId, LocalDate today) {
        // ✅ 일단 “간단 버전(정공법 최소)”:
        // daily streak 로직이 따로 있으면 그걸 그대로 쓰고,
        // 없으면 0으로 두고 나중에 확장해도 됨.
        return 0;
    }
}
