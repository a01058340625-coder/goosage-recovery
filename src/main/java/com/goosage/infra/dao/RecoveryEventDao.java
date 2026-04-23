package com.goosage.infra.dao;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.goosage.domain.EventType;

@Repository
public class RecoveryEventDao {

    private static final Logger log =
            LoggerFactory.getLogger(RecoveryEventDao.class);

    private final JdbcTemplate jdbcTemplate;

    public RecoveryEventDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void recordEvent(long userId,
                            EventType eventType,
                            String refType,
                            Long refId,
                            String payloadJson,
                            String source) {
        Long sessionId = findActiveSessionId(userId);

        if (sessionId == null) {
            sessionId = createSession(userId);
        }

        insertEvent(sessionId, userId, eventType, refType, refId, payloadJson, source);
        touchSession(sessionId);
        upsertDaily(userId, LocalDate.now(), eventType);
    }

    private Long findActiveSessionId(long userId) {
        String sql = """
            SELECT id
            FROM recovery_sessions
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
            INSERT INTO recovery_sessions (user_id, started_at, total_events, last_event_at)
            VALUES (?, NOW(), 0, NOW())
        """;

        jdbcTemplate.update(sql, userId);

        Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        return (id == null) ? 0L : id;
    }

    private void insertEvent(long sessionId,
                             long userId,
                             EventType eventType,
                             String refType,
                             Long refId,
                             String payloadJson,
                             String source) {
        String sql = """
            INSERT INTO recovery_events (
                session_id,
                user_id,
                type,
                event_type,
                ref_type,
                ref_id,
                payload_json,
                source,
                created_at
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())
        """;

        jdbcTemplate.update(
                sql,
                sessionId,
                userId,
                eventType.name(),
                eventType.name(),
                refType,
                refId,
                payloadJson,
                source
        );

        log.info(
                "[RECOVERY_EVENT][INSERT] sessionId={}, userId={}, eventType={}, source={}",
                sessionId,
                userId,
                eventType.name(),
                source
        );
    }

    private void touchSession(long sessionId) {
        String sql = """
            UPDATE recovery_sessions
            SET total_events = total_events + 1,
                last_event_at = NOW()
            WHERE id = ?
        """;

        jdbcTemplate.update(sql, sessionId);
    }

    private void upsertDaily(long userId, LocalDate ymd, EventType eventType) {
        int quizInc = (eventType == EventType.BET_ATTEMPT) ? 1 : 0;
        int wrongInc = (eventType == EventType.BET_BLOCKED) ? 1 : 0;
        int wrongDoneInc = (eventType == EventType.RECOVERY_ACTION) ? 1 : 0;

        String sql = """
            INSERT INTO recovery_daily_learning (
                user_id,
                ymd,
                events_count,
                quiz_submits,
                wrong_reviews,
                wrong_review_done_count,
                last_event_at
            )
            VALUES (?, ?, 1, ?, ?, ?, NOW())
            ON DUPLICATE KEY UPDATE
                events_count = events_count + 1,
                quiz_submits = quiz_submits + VALUES(quiz_submits),
                wrong_reviews = wrong_reviews + VALUES(wrong_reviews),
                wrong_review_done_count = wrong_review_done_count + VALUES(wrong_review_done_count),
                last_event_at = NOW()
        """;

        jdbcTemplate.update(
            sql,
            userId,
            java.sql.Date.valueOf(ymd),
            quizInc,
            wrongInc,
            wrongDoneInc
        );
    }
}