package com.goosage.academy.course;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class AcademyCourseDao {

    private final JdbcTemplate jdbcTemplate;

    public AcademyCourseDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long insertCourse(long workspaceId, String title, String description) {
        String sql = """
            INSERT INTO academy_course(workspace_id, title, description, is_active, created_at)
            VALUES(?,?,?,1,NOW())
            """;
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, workspaceId);
            ps.setString(2, title);
            ps.setString(3, description);
            return ps;
        }, kh);

        Number key = kh.getKey();
        if (key == null) throw new RuntimeException("course id 생성 실패");
        return key.longValue();
    }

    public long insertCourseItem(long courseId, long knowledgeId, int orderIndex) {
        String sql = """
            INSERT INTO academy_course_item(course_id, knowledge_id, order_index)
            VALUES(?,?,?)
            """;
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, courseId);
            ps.setLong(2, knowledgeId);
            ps.setInt(3, orderIndex);
            return ps;
        }, kh);

        Number key = kh.getKey();
        if (key == null) throw new RuntimeException("course_item id 생성 실패");
        return key.longValue();
    }

    public Optional<CourseRow> findCourse(long courseId) {
        String sql = """
            SELECT id, workspace_id, title, description, is_active, created_at
            FROM academy_course
            WHERE id = ?
            """;
        List<CourseRow> list = jdbcTemplate.query(sql, (rs, rowNum) ->
                new CourseRow(
                        rs.getLong("id"),
                        rs.getLong("workspace_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getBoolean("is_active"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ), courseId);

        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public List<CourseItemResponse> findCourseItems(long courseId) {
        String sql = """
            SELECT id, knowledge_id, order_index
            FROM academy_course_item
            WHERE course_id = ?
            ORDER BY order_index ASC, id ASC
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new CourseItemResponse(
                        rs.getLong("id"),
                        rs.getLong("knowledge_id"),
                        rs.getInt("order_index")
                ), courseId);
    }

    public boolean isMemberOfWorkspace(long userId, long workspaceId) {
        String sql = """
            SELECT COUNT(*)
            FROM academy_member
            WHERE workspace_id = ? AND user_id = ?
            """;
        Integer c = jdbcTemplate.queryForObject(sql, Integer.class, workspaceId, userId);
        return c != null && c > 0;
    }

    public record CourseRow(long id, long workspaceId, String title, String description,
                            boolean active, LocalDateTime createdAt) {}
}
