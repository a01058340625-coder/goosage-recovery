package com.goosage.academy.report;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AcademyReportDao {

    private final JdbcTemplate jdbcTemplate;

    public AcademyReportDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int countTotalItems(long courseId) {
        Integer n = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM academy_course_item WHERE course_id=?",
                Integer.class, courseId
        );
        return n == null ? 0 : n;
    }

    public int countEnrolledUsers(long courseId) {
        Integer n = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM academy_enrollment WHERE course_id=?",
                Integer.class, courseId
        );
        return n == null ? 0 : n;
    }

    public int countActiveUsers(long courseId) {
        Integer n = jdbcTemplate.queryForObject(
                "SELECT COUNT(DISTINCT user_id) FROM academy_progress WHERE course_id=?",
                Integer.class, courseId
        );
        return n == null ? 0 : n;
    }

    public int countCompletedUsers(long courseId, int totalItems) {
        if (totalItems <= 0) return 0;

        Integer n = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*) FROM (
                  SELECT user_id
                  FROM academy_progress
                  WHERE course_id=? AND status='DONE'
                  GROUP BY user_id
                  HAVING COUNT(*) >= ?
                ) t
                """,
                Integer.class, courseId, totalItems
        );
        return n == null ? 0 : n;
    }

    public int avgCompletionPercent(long courseId, int totalItems) {
        if (totalItems <= 0) return 0;

        Double avg = jdbcTemplate.queryForObject(
                """
                SELECT AVG(done_cnt / ? * 100.0) AS avg_percent
                FROM (
                  SELECT e.user_id,
                         COALESCE(SUM(CASE WHEN p.status='DONE' THEN 1 ELSE 0 END), 0) AS done_cnt
                  FROM academy_enrollment e
                  LEFT JOIN academy_progress p
                    ON p.course_id=e.course_id
                   AND p.user_id=e.user_id
                  WHERE e.course_id=?
                  GROUP BY e.user_id
                ) x
                """,
                Double.class, totalItems, courseId
        );
        return avg == null ? 0 : (int) Math.round(avg);
    }

    public LocalDateTime lastActivityAt(long courseId) {
        Timestamp ts = jdbcTemplate.queryForObject(
                """
                SELECT MAX(COALESCE(completed_at, updated_at)) AS last_at
                FROM academy_progress
                WHERE course_id=?
                """,
                Timestamp.class, courseId
        );
        return ts == null ? null : ts.toLocalDateTime();
    }

    public List<CourseReportResponse.StuckItem> topStuckItems(long courseId, int limit) {
        // “막힘” 정의: enrolled 중에서 해당 item을 DONE 처리하지 않은 유저 수가 많은 순
        // stuckUsers = enrolledUsers - doneUsersForItem
        return jdbcTemplate.query(
                """
                SELECT
                  i.id AS item_id,
                  i.knowledge_id AS knowledge_id,
                  (
                    (SELECT COUNT(*) FROM academy_enrollment e WHERE e.course_id=i.course_id)
                    -
                    (SELECT COUNT(DISTINCT p.user_id)
                     FROM academy_progress p
                     WHERE p.course_id=i.course_id AND p.item_id=i.id AND p.status='DONE')
                  ) AS stuck_users
                FROM academy_course_item i
                WHERE i.course_id=?
                ORDER BY stuck_users DESC, i.order_index ASC, i.id ASC
                LIMIT ?
                """,
                (rs, rowNum) -> new CourseReportResponse.StuckItem(
                        rs.getLong("item_id"),
                        rs.getLong("knowledge_id"),
                        rs.getInt("stuck_users")
                ),
                courseId, limit
        );
    }
    
    public List<CourseReportResponse.TopWrongKnowledge> topWrongKnowledges(long courseId, int limit) {
        return jdbcTemplate.query(
            """
            SELECT
              i.knowledge_id AS knowledge_id,
              SUM(qr.wrong_count) AS wrong_count
            FROM academy_course_item i
            JOIN quiz_results qr
              ON qr.knowledge_id = i.knowledge_id
            WHERE i.course_id = ?
            GROUP BY i.knowledge_id
            ORDER BY wrong_count DESC, knowledge_id ASC
            LIMIT ?
            """,
            (rs, rowNum) -> new CourseReportResponse.TopWrongKnowledge(
                rs.getLong("knowledge_id"),
                rs.getInt("wrong_count")
            ),
            courseId, limit
        );
    }
    
    public List<CourseUserReportResponse> findUserReports(long courseId) {
        return jdbcTemplate.query(
            """
            SELECT
              e.user_id AS user_id,

              -- total (course 전체 아이템 수)
              (SELECT COUNT(*) FROM academy_course_item WHERE course_id = e.course_id) AS total,

              -- done (DONE 처리한 아이템 수)
              (
                SELECT COUNT(*)
                FROM academy_progress p
                WHERE p.user_id = e.user_id
                  AND p.course_id = e.course_id
                  AND p.status = 'DONE'
              ) AS done,

              -- lastActivityAt (QUIZ_SUBMIT 기준)
              (
                SELECT MAX(se.created_at)
                FROM study_events se
                WHERE se.user_id = e.user_id
                  AND se.event_type = 'QUIZ_SUBMIT'
                  AND se.ref_type = 'KNOWLEDGE'
                  AND se.ref_id IN (
                    SELECT knowledge_id
                    FROM academy_course_item
                    WHERE course_id = e.course_id
                  )
              ) AS lastActivityAt,

              -- wrongSum (해당 코스 지식들에 대한 quiz_results wrong_count 합)
              (
                SELECT COALESCE(SUM(qr.wrong_count), 0)
                FROM quiz_results qr
                WHERE qr.user_id = e.user_id
                  AND qr.knowledge_id IN (
                    SELECT knowledge_id
                    FROM academy_course_item
                    WHERE course_id = e.course_id
                  )
              ) AS wrongSum

            FROM academy_enrollment e
            WHERE e.course_id = ?
            ORDER BY e.user_id ASC
            """,
            (rs, rowNum) -> new CourseUserReportResponse(
                rs.getLong("user_id"),
                rs.getInt("done"),
                rs.getInt("total"),
                (rs.getInt("total") == 0) ? 0 : (rs.getInt("done") * 100 / rs.getInt("total")),
                rs.getTimestamp("lastActivityAt") == null ? null : rs.getTimestamp("lastActivityAt").toLocalDateTime(),
                rs.getInt("wrongSum")
            ),
            courseId
        );
    }
}