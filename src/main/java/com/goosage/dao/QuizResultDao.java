package com.goosage.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class QuizResultDao {

    private final JdbcTemplate jdbcTemplate;

    public QuizResultDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ✅ v0.6 보강: user_id 포함 저장
    public void save(long userId, long knowledgeId, int totalCount, int correctCount, int scorePercent, String detailsJson) {

        String sql = """
            INSERT INTO quiz_results
            (user_id, knowledge_id, total_count, correct_count, score_percent, details_json)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(sql, userId, knowledgeId, totalCount, correctCount, scorePercent, detailsJson);
    }

    // ✅ 결과 조회(지식 기준) - 필요하면 유지
    public List<QuizResultRow> findByKnowledgeId(long knowledgeId) {
        String sql = """
            SELECT id, user_id, knowledge_id, total_count, correct_count, score_percent, details_json, created_at
            FROM quiz_results
            WHERE knowledge_id = ?
            ORDER BY id DESC
        """;

        return jdbcTemplate.query(sql, (rs, i) -> new QuizResultRow(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getLong("knowledge_id"),
                rs.getInt("total_count"),
                rs.getInt("correct_count"),
                rs.getInt("score_percent"),
                rs.getString("details_json"),
                rs.getTimestamp("created_at").toLocalDateTime()
        ), knowledgeId);
    }

    // ✅ 최신 결과 1건(지식 기준) - 필요하면 유지
    public QuizResultRow findLatestByKnowledgeId(long knowledgeId) {
        String sql = """
            SELECT id, user_id, knowledge_id, total_count, correct_count, score_percent, details_json, created_at
            FROM quiz_results
            WHERE knowledge_id = ?
            ORDER BY id DESC
            LIMIT 1
        """;

        List<QuizResultRow> rows = jdbcTemplate.query(sql, (rs, i) -> new QuizResultRow(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getLong("knowledge_id"),
                rs.getInt("total_count"),
                rs.getInt("correct_count"),
                rs.getInt("score_percent"),
                rs.getString("details_json"),
                rs.getTimestamp("created_at").toLocalDateTime()
        ), knowledgeId);

        return rows.isEmpty() ? null : rows.get(0);
    }

    // ✅ v0.7 핵심: "유저 + 지식" 기준 최신 결과
    public QuizResultRow findLatestByUserAndKnowledgeId(long userId, long knowledgeId) {
        String sql = """
            SELECT id, user_id, knowledge_id, total_count, correct_count, score_percent, details_json, created_at
            FROM quiz_results
            WHERE user_id = ? AND knowledge_id = ?
            ORDER BY id DESC
            LIMIT 1
        """;

        List<QuizResultRow> rows = jdbcTemplate.query(sql, (rs, i) -> new QuizResultRow(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getLong("knowledge_id"),
                rs.getInt("total_count"),
                rs.getInt("correct_count"),
                rs.getInt("score_percent"),
                rs.getString("details_json"),
                rs.getTimestamp("created_at").toLocalDateTime()
        ), userId, knowledgeId);

        return rows.isEmpty() ? null : rows.get(0);
    }

    // ✅ Row 타입 (v0.7부터 userId/knowledgeId 포함)
    public static record QuizResultRow(
            long id,
            long userId,
            long knowledgeId,
            int totalCount,
            int correctCount,
            int scorePercent,
            String detailsJson,
            LocalDateTime createdAt
    ) {}
}
