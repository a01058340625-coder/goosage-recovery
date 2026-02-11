// src/main/java/com/goosage/repository/StatsDao.java
package com.goosage.infra.dao;

import java.time.LocalDate;
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

    /** ✅ 최근 N일 전체 시도 횟수 */
    public long attemptsInDays(long userId, int days) {
        String sql = """
            SELECT COUNT(*)
            FROM quiz_results
            WHERE user_id = ?
              AND created_at >= (CURRENT_DATE - INTERVAL ? DAY)
        """;
        Long v = jdbcTemplate.queryForObject(sql, Long.class, userId, days);
        return (v == null) ? 0L : v;
    }

    /** ✅ 최근 N일 평균 점수 */
    public double avgScoreInDays(long userId, int days) {
        String sql = """
            SELECT COALESCE(AVG(score_percent), 0)
            FROM quiz_results
            WHERE user_id = ?
              AND created_at >= (CURRENT_DATE - INTERVAL ? DAY)
        """;
        Double v = jdbcTemplate.queryForObject(sql, Double.class, userId, days);
        return (v == null) ? 0.0 : v;
    }

    /** ✅ 오늘 시도 횟수 */
    public long attemptsToday(long userId) {
        String sql = """
            SELECT COUNT(*)
            FROM quiz_results
            WHERE user_id = ?
              AND DATE(created_at) = CURRENT_DATE
        """;
        Long v = jdbcTemplate.queryForObject(sql, Long.class, userId);
        return (v == null) ? 0L : v;
    }

    /** ✅ 오늘 평균 점수 */
    public double avgScoreToday(long userId) {
        String sql = """
            SELECT COALESCE(AVG(score_percent), 0)
            FROM quiz_results
            WHERE user_id = ?
              AND DATE(created_at) = CURRENT_DATE
        """;
        Double v = jdbcTemplate.queryForObject(sql, Double.class, userId);
        return (v == null) ? 0.0 : v;
    }

    /** ✅ 최근 결과 N개 */
    public List<StatsOverviewResponse.RecentAttempt> recentAttempts(long userId, int limit) {
        String sql = """
            SELECT id AS result_id, knowledge_id, score_percent, created_at
            FROM quiz_results
            WHERE user_id = ?
            ORDER BY id DESC
            LIMIT ?
        """;

        return jdbcTemplate.query(sql, (rs, i) ->
                new StatsOverviewResponse.RecentAttempt(
                        rs.getLong("result_id"),
                        rs.getLong("knowledge_id"),
                        rs.getInt("score_percent"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ), userId, limit);
    }

    /**
     * ✅ v0.8.3 Readable Insight:
     * "오답 Top Knowledge"에 title까지 붙여서 내려준다.
     * - knowledge가 없으면 '(missing knowledge)'로 표시
     */
    public List<StatsOverviewResponse.WrongTopDetail> wrongTopKnowledge(long userId, int limit) {
        String sql = """
            SELECT
                qr.knowledge_id,
                COALESCE(k.title, '(missing knowledge)') AS title,
                SUM(qr.total_count - qr.correct_count) AS wrong_count
            FROM quiz_results qr
            LEFT JOIN knowledge k ON k.id = qr.knowledge_id
            WHERE qr.user_id = ?
            GROUP BY qr.knowledge_id, k.title
            ORDER BY wrong_count DESC
            LIMIT ?
        """;

        return jdbcTemplate.query(sql, (rs, i) ->
                new StatsOverviewResponse.WrongTopDetail(
                        rs.getLong("knowledge_id"),
                        rs.getString("title"),
                        rs.getLong("wrong_count")
                ), userId, limit);
    }
}
