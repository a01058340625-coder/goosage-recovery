package com.goosage.repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.goosage.dto.StatsOverviewResponse;

@Repository
public class StatsDao {

    private final JdbcTemplate jdbcTemplate;

    public StatsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long totalAttempts(long userId) {
        String sql = "SELECT COUNT(*) FROM quiz_results WHERE user_id = ?";
        Long v = jdbcTemplate.queryForObject(sql, Long.class, userId);
        return v == null ? 0 : v;
    }

    public double avgScorePercent(long userId) {
        String sql = "SELECT COALESCE(AVG(score_percent), 0) FROM quiz_results WHERE user_id = ?";
        Double v = jdbcTemplate.queryForObject(sql, Double.class, userId);
        return v == null ? 0.0 : v;
    }

    public long todayAttempts(long userId) {
        // ✅ created_at이 TIMESTAMP이므로 날짜 비교는 이렇게가 안전
        String sql = "SELECT COUNT(*) FROM quiz_results WHERE user_id = ? AND DATE(created_at) = CURDATE()";
        Long v = jdbcTemplate.queryForObject(sql, Long.class, userId);
        return v == null ? 0 : v;
    }

    public double todayAvgScorePercent(long userId) {
        String sql = "SELECT COALESCE(AVG(score_percent), 0) FROM quiz_results WHERE user_id = ? AND DATE(created_at) = CURDATE()";
        Double v = jdbcTemplate.queryForObject(sql, Double.class, userId);
        return v == null ? 0.0 : v;
    }

    public List<StatsOverviewResponse.RecentAttempt> recentAttempts(long userId, int limit) {
        String sql = """
            SELECT id, knowledge_id, score_percent, created_at
            FROM quiz_results
            WHERE user_id = ?
            ORDER BY id DESC
            LIMIT ?
        """;

        return jdbcTemplate.query(sql, (rs, i) ->
                new StatsOverviewResponse.RecentAttempt(
                        rs.getLong("id"),
                        rs.getLong("knowledge_id"),
                        rs.getInt("score_percent"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ), userId, limit);
    }

    public List<StatsOverviewResponse.WrongTop> wrongTopKnowledge(long userId, int limit) {
        // ✅ “틀린 개수” = (total_count - correct_count) 합
        String sql = """
            SELECT knowledge_id, SUM(total_count - correct_count) AS wrong_count
            FROM quiz_results
            WHERE user_id = ?
            GROUP BY knowledge_id
            ORDER BY wrong_count DESC
            LIMIT ?
        """;

        return jdbcTemplate.query(sql, (rs, i) ->
                new StatsOverviewResponse.WrongTop(
                        rs.getLong("knowledge_id"),
                        rs.getLong("wrong_count")
                ), userId, limit);
    }
    
    public long attemptsInDays(long userId, int days) {
        String sql = """
            SELECT COUNT(*)
            FROM quiz_results
            WHERE user_id = ?
              AND created_at >= (NOW() - INTERVAL ? DAY)
        """;
        Long v = jdbcTemplate.queryForObject(sql, Long.class, userId, days);
        return v == null ? 0 : v;
    }

    public double avgScoreInDays(long userId, int days) {
        String sql = """
            SELECT COALESCE(AVG(score_percent), 0)
            FROM quiz_results
            WHERE user_id = ?
              AND created_at >= (NOW() - INTERVAL ? DAY)
        """;
        Double v = jdbcTemplate.queryForObject(sql, Double.class, userId, days);
        return v == null ? 0.0 : v;
    }

    public List<StatsOverviewResponse.RecentAttempt> recentAttemptsInDays(long userId, int days, int limit) {
        String sql = """
            SELECT id, knowledge_id, score_percent, created_at
            FROM quiz_results
            WHERE user_id = ?
              AND created_at >= (NOW() - INTERVAL ? DAY)
            ORDER BY id DESC
            LIMIT ?
        """;
        return jdbcTemplate.query(sql, (rs, i) ->
            new StatsOverviewResponse.RecentAttempt(
                rs.getLong("id"),
                rs.getLong("knowledge_id"),
                rs.getInt("score_percent"),
                rs.getTimestamp("created_at").toLocalDateTime()
            ), userId, days, limit);
    }

    public List<StatsOverviewResponse.WrongTop> wrongTopKnowledgeInDays(long userId, int days, int limit) {
        String sql = """
            SELECT knowledge_id, SUM(total_count - correct_count) AS wrong_count
            FROM quiz_results
            WHERE user_id = ?
              AND created_at >= (NOW() - INTERVAL ? DAY)
            GROUP BY knowledge_id
            ORDER BY wrong_count DESC
            LIMIT ?
        """;
        return jdbcTemplate.query(sql, (rs, i) ->
            new StatsOverviewResponse.WrongTop(
                rs.getLong("knowledge_id"),
                rs.getLong("wrong_count")
            ), userId, days, limit);
    }

}
