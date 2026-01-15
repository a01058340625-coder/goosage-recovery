package com.goosage.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.goosage.dto.QuizItemDto;

@Repository
public class QuizItemDao {

    private final JdbcTemplate jdbcTemplate;

    public QuizItemDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<QuizItemDto> findByKnowledgeId(long knowledgeId) {
        String sql = """
            SELECT id, knowledge_id, no, question, expected, created_at
            FROM quiz_items
            WHERE knowledge_id = ?
            ORDER BY no ASC
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            QuizItemDto dto = new QuizItemDto();
            dto.setId(rs.getLong("id"));
            dto.setKnowledgeId(rs.getLong("knowledge_id"));
            dto.setNo(rs.getInt("no"));
            dto.setQuestion(rs.getString("question"));
            dto.setExpected(rs.getString("expected"));
            dto.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return dto;
        }, knowledgeId);
    }

    public int insert(long knowledgeId, int no, String question, String expected) {
        String sql = """
            INSERT INTO quiz_items(knowledge_id, no, question, expected)
            VALUES(?,?,?,?)
        """;
        return jdbcTemplate.update(sql, knowledgeId, no, question, expected);
    }

    public boolean exists(long knowledgeId) {
        String sql = "SELECT COUNT(*) FROM quiz_items WHERE knowledge_id = ?";
        Integer cnt = jdbcTemplate.queryForObject(sql, Integer.class, knowledgeId);
        return cnt != null && cnt > 0;
    }
}
