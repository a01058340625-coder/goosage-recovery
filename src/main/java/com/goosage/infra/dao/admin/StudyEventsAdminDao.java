package com.goosage.infra.dao.admin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StudyEventsAdminDao {

    private final JdbcTemplate jdbcTemplate;

    public StudyEventsAdminDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<StudyEventRow> recent(long userId, int limit) {
        String sql = """
            SELECT id, user_id, type, created_at
            FROM recovery_events
            WHERE user_id = ?
            ORDER BY created_at DESC
            LIMIT ?
        """;

        return jdbcTemplate.query(sql, this::map, userId, limit);
    }

    private StudyEventRow map(ResultSet rs, int rowNum) throws SQLException {
        return new StudyEventRow(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getString("type"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }

    public record StudyEventRow(
            long id,
            long userId,
            String type,
            java.time.LocalDateTime createdAt
    ) {}
}