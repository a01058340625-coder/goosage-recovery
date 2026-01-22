package com.goosage.dao;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StudyEventDao {

    private final JdbcTemplate jdbcTemplate;

    public StudyEventDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
   
    public void recordEvent(long userId, String eventType, String refType, Long refId, String payloadJson) {
        // 1) 활성 세션 찾기(ended_at is null) - 가장 최근 1개
        Long sessionId = findActiveSessionId(userId);

        // 2) 없으면 새 세션 생성
        if (sessionId == null) {
            sessionId = createSession(userId);
        }

        // 3) 이벤트 insert
        insertEvent(sessionId, userId, eventType, refType, refId, payloadJson);

        // 4) 세션 집계 업데이트
        touchSession(sessionId);

        // 5) daily_learning upsert (Asia/Seoul 기준으로 서버 LocalDate 쓰면 OK)
        upsertDaily(userId, LocalDate.now(), eventType);
    }

    private Long findActiveSessionId(long userId) {
        String sql = """
            SELECT id
            FROM study_sessions
            WHERE user_id = ?
              AND ended_at IS NULL
            ORDER BY started_at DESC
            LIMIT 1
        """;
        var list = jdbcTemplate.query(sql, (rs, i) -> rs.getLong("id"), userId);
        return list.isEmpty() ? null : list.get(0);
    }

    private long createSession(long userId) {
        String sql = """
            INSERT INTO study_sessions (user_id, started_at, total_events, last_event_at)
            VALUES (?, NOW(), 0, NOW())
        """;
        jdbcTemplate.update(sql, userId);
        Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        return (id == null) ? 0L : id;
    }

    private void insertEvent(long sessionId, long userId, String eventType, String refType, Long refId, String payloadJson) {
        String sql = """
            INSERT INTO study_events (session_id, user_id, event_type, ref_type, ref_id, payload_json, created_at)
            VALUES (?, ?, ?, ?, ?, ?, NOW())
        """;
        jdbcTemplate.update(sql,
                sessionId,
                userId,
                eventType,
                refType,
                refId,
                payloadJson
        );
    }

    private void touchSession(long sessionId) {
        String sql = """
            UPDATE study_sessions
            SET total_events = total_events + 1,
                last_event_at = NOW()
            WHERE id = ?
        """;
        jdbcTemplate.update(sql, sessionId);
    }

    private void upsertDaily(long userId, LocalDate ymd, String eventType) {
        // 이벤트 타입별 카운트 (초기 v0.9 기준: QUIZ_SUBMIT, WRONG_REVIEW만 별도)
        int quizInc = "QUIZ_SUBMIT".equals(eventType) ? 1 : 0;
        int wrongInc = "WRONG_REVIEW".equals(eventType) ? 1 : 0;

        String sql = """
            INSERT INTO daily_learning (user_id, ymd, events_count, quiz_submits, wrong_reviews, last_event_at)
            VALUES (?, ?, 1, ?, ?, NOW())
            ON DUPLICATE KEY UPDATE
                events_count = events_count + 1,
                quiz_submits = quiz_submits + VALUES(quiz_submits),
                wrong_reviews = wrong_reviews + VALUES(wrong_reviews),
                last_event_at = NOW()
        """;

        jdbcTemplate.update(sql,
                userId,
                java.sql.Date.valueOf(ymd),
                quizInc,
                wrongInc
        );
    }
}
