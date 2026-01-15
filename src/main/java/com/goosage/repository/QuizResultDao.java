package com.goosage.repository;

import java.sql.Timestamp;
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

    public void save(long knowledgeId,
                     int totalCount,
                     int correctCount,
                     int scorePercent,
                     String detailsJson) {

        String sql = """
            INSERT INTO quiz_results
            (knowledge_id, total_count, correct_count, score_percent, details_json)
            VALUES (?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(
            sql,
            knowledgeId,
            totalCount,
            correctCount,
            scorePercent,
            detailsJson
        );
    }

    // ✅ 추가: 결과 조회
    public List<QuizResultRow> findByKnowledgeId(long knowledgeId) {
        String sql = """
            SELECT id, total_count, correct_count, score_percent, details_json, created_at
            FROM quiz_results
            WHERE knowledge_id = ?
            ORDER BY id DESC
        """;

        return jdbcTemplate.query(sql, (rs, i) ->
            new QuizResultRow(
                rs.getLong("id"),
                rs.getInt("total_count"),
                rs.getInt("correct_count"),
                rs.getInt("score_percent"),
                rs.getString("details_json"),
                rs.getTimestamp("created_at").toLocalDateTime()
            ),
            knowledgeId
        );
    }
    
 // ✅ 추가: 최신 결과 1건 조회 (v0.7)
    public QuizResultRow findLatestByKnowledgeId(long knowledgeId) {
        String sql = """
            SELECT id, total_count, correct_count, score_percent, details_json, created_at
            FROM quiz_results
            WHERE knowledge_id = ?
            ORDER BY id DESC
            LIMIT 1
        """;

        List<QuizResultRow> rows = jdbcTemplate.query(sql, (rs, i) ->
            new QuizResultRow(
                rs.getLong("id"),
                rs.getInt("total_count"),
                rs.getInt("correct_count"),
                rs.getInt("score_percent"),
                rs.getString("details_json"),
                rs.getTimestamp("created_at").toLocalDateTime()
            ),
            knowledgeId
        );

        return rows.isEmpty() ? null : rows.get(0);
    }



    // ✅ 추가: Row 타입 (파일 안에 같이 둬도 됨)
    public static record QuizResultRow(
        long id,
        int totalCount,
        int correctCount,
        int scorePercent,
        String detailsJson,
        LocalDateTime createdAt
    ) {}
}
