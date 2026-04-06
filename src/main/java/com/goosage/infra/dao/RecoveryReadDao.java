package com.goosage.infra.dao;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RecoveryReadDao {

    private final JdbcTemplate jdbcTemplate;

    public RecoveryReadDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<TodayRowRecord> findToday(long userId) {

        String sql =
                "SELECT " +
                "  DATE(MAX(created_at)) AS ymd, " +
                "  COUNT(*) AS events_count, " +
                "  SUM(CASE WHEN type = 'BET_ATTEMPT' THEN 1 ELSE 0 END) AS quiz_submits, " +
                "  SUM(CASE WHEN type = 'BET_BLOCKED' THEN 1 ELSE 0 END) AS wrong_reviews, " +
                "  SUM(CASE WHEN type = 'RECOVERY_ACTION' THEN 1 ELSE 0 END) AS wrong_review_done_count " +
                "FROM study_events " +
                "WHERE user_id = ? " +
                "  AND DATE(created_at) = CURDATE() " +
                "HAVING COUNT(*) > 0";

        try {
            TodayRowRecord row = jdbcTemplate.queryForObject(
                    sql,
                    (rs, rowNum) -> new TodayRowRecord(
                            rs.getDate("ymd").toLocalDate(),
                            rs.getInt("events_count"),
                            rs.getInt("quiz_submits"),
                            rs.getInt("wrong_reviews"),
                            rs.getInt("wrong_review_done_count")
                    ),
                    userId
            );

            return Optional.ofNullable(row);

        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<LocalDateTime> lastEventAtAll(long userId) {
        String sql = "SELECT MAX(created_at) FROM study_events WHERE user_id = ?";
        Timestamp ts = jdbcTemplate.queryForObject(sql, Timestamp.class, userId);
        return (ts == null) ? Optional.empty() : Optional.of(ts.toLocalDateTime());
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

        LocalDate anchor = today;
        if (!days.contains(today)) {
            anchor = today.minusDays(1);
        }

        int streak = 0;
        LocalDate cursor = anchor;

        while (days.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }

        return streak;
    }

    public int todayEventCountFromEvents(long userId, LocalDate today) {

        String sql =
                "SELECT COUNT(*) " +
                "FROM study_events " +
                "WHERE user_id = ? " +
                "  AND created_at >= ? " +
                "  AND created_at < ?";

        Timestamp from = Timestamp.valueOf(today.atStartOfDay());
        Timestamp to   = Timestamp.valueOf(today.plusDays(1).atStartOfDay());

        Integer cnt = jdbcTemplate.queryForObject(sql, Integer.class, userId, from, to);
        return (cnt == null) ? 0 : cnt;
    }

    public int recentEventCount3d(long userId, LocalDate today) {

        String sql =
                "SELECT COUNT(*) " +
                "FROM study_events " +
                "WHERE user_id = ? " +
                "  AND created_at >= ? " +
                "  AND created_at < ?";

        Timestamp from = Timestamp.valueOf(today.minusDays(2).atStartOfDay());
        Timestamp to   = Timestamp.valueOf(today.plusDays(1).atStartOfDay());

        Integer cnt = jdbcTemplate.queryForObject(sql, Integer.class, userId, from, to);
        return (cnt == null) ? 0 : cnt;
    }

    public int recentWrong3d(long userId, LocalDate today) {

        String sql =
                "SELECT COUNT(*) " +
                "FROM study_events " +
                "WHERE user_id = ? " +
                "  AND type = 'BET_BLOCKED' " +
                "  AND created_at >= ? " +
                "  AND created_at < ?";

        Timestamp from = Timestamp.valueOf(today.minusDays(2).atStartOfDay());
        Timestamp to   = Timestamp.valueOf(today.plusDays(1).atStartOfDay());

        Integer cnt = jdbcTemplate.queryForObject(sql, Integer.class, userId, from, to);
        return (cnt == null) ? 0 : cnt;
    }

    public int recentWrongDone3d(long userId, LocalDate today) {

        String sql =
                "SELECT COUNT(*) " +
                "FROM study_events " +
                "WHERE user_id = ? " +
                "  AND type = 'RECOVERY_ACTION' " +
                "  AND created_at >= ? " +
                "  AND created_at < ?";

        Timestamp from = Timestamp.valueOf(today.minusDays(2).atStartOfDay());
        Timestamp to   = Timestamp.valueOf(today.plusDays(1).atStartOfDay());

        Integer cnt = jdbcTemplate.queryForObject(sql, Integer.class, userId, from, to);
        return (cnt == null) ? 0 : cnt;
    }

    public int todayWrongFromEvents(long userId, LocalDate today) {

        String sql =
                "SELECT COUNT(*) " +
                "FROM study_events " +
                "WHERE user_id = ? " +
                "  AND type = 'BET_BLOCKED' " +
                "  AND created_at >= ? " +
                "  AND created_at < ?";

        Timestamp from = Timestamp.valueOf(today.atStartOfDay());
        Timestamp to   = Timestamp.valueOf(today.plusDays(1).atStartOfDay());

        Integer cnt = jdbcTemplate.queryForObject(sql, Integer.class, userId, from, to);
        return (cnt == null) ? 0 : cnt;
    }

    public int todayWrongDoneFromEvents(long userId, LocalDate today) {

        String sql =
                "SELECT COUNT(*) " +
                "FROM study_events " +
                "WHERE user_id = ? " +
                "  AND type = 'RECOVERY_ACTION' " +
                "  AND created_at >= ? " +
                "  AND created_at < ?";

        Timestamp from = Timestamp.valueOf(today.atStartOfDay());
        Timestamp to   = Timestamp.valueOf(today.plusDays(1).atStartOfDay());

        Integer cnt = jdbcTemplate.queryForObject(sql, Integer.class, userId, from, to);
        return (cnt == null) ? 0 : cnt;
    }

    public int todayQuizFromEvents(long userId, LocalDate today) {

        String sql =
                "SELECT COUNT(*) " +
                "FROM study_events " +
                "WHERE user_id = ? " +
                "  AND type = 'BET_ATTEMPT' " +
                "  AND created_at >= ? " +
                "  AND created_at < ?";

        Timestamp from = Timestamp.valueOf(today.atStartOfDay());
        Timestamp to   = Timestamp.valueOf(today.plusDays(1).atStartOfDay());

        Integer cnt = jdbcTemplate.queryForObject(sql, Integer.class, userId, from, to);
        return (cnt == null) ? 0 : cnt;
    }
}