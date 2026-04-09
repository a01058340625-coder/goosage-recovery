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
                "  SUM(CASE WHEN type = 'URGE_LOG' THEN 1 ELSE 0 END) AS urge_logs, " +
                "  SUM(CASE WHEN type = 'BET_ATTEMPT' THEN 1 ELSE 0 END) AS bet_attempts, " +
                "  SUM(CASE WHEN type = 'BET_BLOCKED' THEN 1 ELSE 0 END) AS bet_blocked_count, " +
                "  SUM(CASE WHEN type = 'RECOVERY_ACTION' THEN 1 ELSE 0 END) AS recovery_action_count, " +
                "  SUM(CASE WHEN type = 'RELAPSE_SIGNAL' THEN 1 ELSE 0 END) AS relapse_signal_count " +
                "FROM recovery_events " +
                "WHERE user_id = ? " +
                "  AND DATE(created_at) = CURDATE() " +
                "HAVING COUNT(*) > 0";

        try {
            TodayRowRecord row = jdbcTemplate.queryForObject(
                    sql,
                    (rs, rowNum) -> new TodayRowRecord(
                            rs.getDate("ymd").toLocalDate(),
                            rs.getInt("events_count"),
                            rs.getInt("urge_logs"),
                            rs.getInt("bet_attempts"),
                            rs.getInt("bet_blocked_count"),
                            rs.getInt("recovery_action_count"),
                            rs.getInt("relapse_signal_count")
                    ),
                    userId
            );

            return Optional.ofNullable(row);

        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<LocalDateTime> lastEventAtAll(long userId) {
        String sql = "SELECT MAX(created_at) FROM recovery_events WHERE user_id = ?";
        Timestamp ts = jdbcTemplate.queryForObject(sql, Timestamp.class, userId);
        return (ts == null) ? Optional.empty() : Optional.of(ts.toLocalDateTime());
    }

    public int calcStreakDays(long userId, LocalDate today) {

        String sql =
                "SELECT DISTINCT DATE(created_at) AS ymd " +
                "FROM recovery_events " +
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
        return countByType(userId, today, null);
    }

    public int recentEventCount3d(long userId, LocalDate today) {
        String sql =
                "SELECT COUNT(*) " +
                "FROM recovery_events " +
                "WHERE user_id = ? " +
                "  AND created_at >= ? " +
                "  AND created_at < ?";

        Timestamp from = Timestamp.valueOf(today.minusDays(2).atStartOfDay());
        Timestamp to   = Timestamp.valueOf(today.plusDays(1).atStartOfDay());

        Integer cnt = jdbcTemplate.queryForObject(sql, Integer.class, userId, from, to);
        return (cnt == null) ? 0 : cnt;
    }

    public int recentUrgeLog3d(long userId, LocalDate today) {
        return countByType3d(userId, today, "URGE_LOG");
    }

    public int recentBetAttempt3d(long userId, LocalDate today) {
        return countByType3d(userId, today, "BET_ATTEMPT");
    }

    public int recentBetBlocked3d(long userId, LocalDate today) {
        return countByType3d(userId, today, "BET_BLOCKED");
    }

    public int recentRecoveryAction3d(long userId, LocalDate today) {
        return countByType3d(userId, today, "RECOVERY_ACTION");
    }

    public int recentRelapseSignal3d(long userId, LocalDate today) {
        return countByType3d(userId, today, "RELAPSE_SIGNAL");
    }

    public int todayUrgeLogFromEvents(long userId, LocalDate today) {
        return countByType(userId, today, "URGE_LOG");
    }

    public int todayBetAttemptFromEvents(long userId, LocalDate today) {
        return countByType(userId, today, "BET_ATTEMPT");
    }

    public int todayBetBlockedFromEvents(long userId, LocalDate today) {
        return countByType(userId, today, "BET_BLOCKED");
    }

    public int todayRecoveryActionFromEvents(long userId, LocalDate today) {
        return countByType(userId, today, "RECOVERY_ACTION");
    }

    public int todayRelapseSignalFromEvents(long userId, LocalDate today) {
        return countByType(userId, today, "RELAPSE_SIGNAL");
    }

    private int countByType(long userId, LocalDate today, String type) {
        String sql =
                "SELECT COUNT(*) " +
                "FROM recovery_events " +
                "WHERE user_id = ? " +
                (type != null ? "  AND type = ? " : "") +
                "  AND created_at >= ? " +
                "  AND created_at < ?";

        Timestamp from = Timestamp.valueOf(today.atStartOfDay());
        Timestamp to   = Timestamp.valueOf(today.plusDays(1).atStartOfDay());

        Integer cnt = (type == null)
                ? jdbcTemplate.queryForObject(sql, Integer.class, userId, from, to)
                : jdbcTemplate.queryForObject(sql, Integer.class, userId, type, from, to);

        return (cnt == null) ? 0 : cnt;
    }

    private int countByType3d(long userId, LocalDate today, String type) {
        String sql =
                "SELECT COUNT(*) " +
                "FROM recovery_events " +
                "WHERE user_id = ? " +
                "  AND type = ? " +
                "  AND created_at >= ? " +
                "  AND created_at < ?";

        Timestamp from = Timestamp.valueOf(today.minusDays(2).atStartOfDay());
        Timestamp to   = Timestamp.valueOf(today.plusDays(1).atStartOfDay());

        Integer cnt = jdbcTemplate.queryForObject(sql, Integer.class, userId, type, from, to);
        return (cnt == null) ? 0 : cnt;
    }
}