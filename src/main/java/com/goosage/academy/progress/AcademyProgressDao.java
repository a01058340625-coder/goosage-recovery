package com.goosage.academy.progress;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AcademyProgressDao {

    private final JdbcTemplate jdbcTemplate;

    public AcademyProgressDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ProgressItemRow> findProgressItems(long userId, long courseId) {
        String sql = """
            SELECT
              i.id           AS item_id,
              i.knowledge_id AS knowledge_id,
              CASE WHEN p.status='DONE' THEN 1 ELSE 0 END AS done
            FROM academy_course_item i
            LEFT JOIN academy_progress p
              ON p.user_id   = ?
             AND p.course_id = ?
             AND p.item_id   = i.id
            WHERE i.course_id = ?
            ORDER BY i.order_index, i.id
            """;

        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> new ProgressItemRow(
                rs.getLong("item_id"),
                rs.getLong("knowledge_id"),
                rs.getInt("done") == 1
            ),
            userId, courseId, courseId
        );
    }

    public record ProgressItemRow(long itemId, long knowledgeId, boolean done) {}



    // ✅ 코스 전체 아이템 수
    public int countTotalItems(long courseId) {
        Integer n = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM academy_course_item WHERE course_id=?",
                Integer.class, courseId
        );
        return (n == null) ? 0 : n;
    }

    // ✅ 내가 완료한 아이템 수
    public int countDoneItems(long userId, long courseId) {
        Integer n = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM academy_progress WHERE user_id=? AND course_id=? AND status='DONE'",
                Integer.class, userId, courseId
        );
        return (n == null) ? 0 : n;
    }

    // ✅ 코스가 속한 workspace_id 얻기 (권한 체크용)
    public Long findWorkspaceIdByCourseId(long courseId) {
        return jdbcTemplate.query(
                "SELECT workspace_id FROM academy_course WHERE id=?",
                rs -> rs.next() ? rs.getLong(1) : null,
                courseId
        );
    }

    // ✅ workspace 멤버인지 확인(너 기존 정책과 동일)
    public boolean isMemberOfWorkspace(long userId, long workspaceId) {
        Integer n = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM academy_member WHERE user_id=? AND workspace_id=?",
                Integer.class, userId, workspaceId
        );
        return n != null && n > 0;
    }
    
    public boolean existsProgress(long userId, long courseId, long itemId) {
        Integer n = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM academy_progress WHERE user_id=? AND course_id=? AND item_id=?",
            Integer.class, userId, courseId, itemId
        );
        return n != null && n > 0;
    }
    
    public int insertProgress(long userId, long courseId, long itemId) {
        return jdbcTemplate.update(
            "INSERT INTO academy_progress (user_id, course_id, item_id, status, updated_at) VALUES (?, ?, ?, 'STARTED', NOW())",
            userId, courseId, itemId
        );
    }
}
