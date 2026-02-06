package com.goosage.dao.study;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StudyReadDao {

	private final JdbcTemplate jdbcTemplate;

	public StudyReadDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Optional<TodayRow> findToday(long userId)
 {
	    // ✅ B안: study_events에서 오늘 집계로 계산
	    return findTodayFromStudyEvents(userId);

	    // 필요하면 A안으로 되돌릴 때:
	    // return findTodayFromDailyLearning(userId);
	}

	private Optional<TodayRow> findTodayFromStudyEvents(long userId) {

	    String sql =
	        "SELECT " +
	        "  DATE(MAX(created_at)) AS ymd, " +
	        "  COUNT(*) AS events_count, " +
	        "  SUM(CASE WHEN event_type = 'QUIZ_SUBMIT' THEN 1 ELSE 0 END) AS quiz_submits, " +
	        "  SUM(CASE WHEN event_type = 'WRONG_REVIEW_DONE' THEN 1 ELSE 0 END) AS wrong_reviews, " +
	        "  MAX(created_at) AS last_event_at " +
	        "FROM study_events " +
	        "WHERE user_id = ? " +
	        "  AND DATE(created_at) = CURDATE()";

	    try {
	        TodayRow row = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {

	            // ymd는 MAX(created_at)이 null이면 null일 수 있으니 방어
	            LocalDate ymd = (rs.getDate("ymd") != null)
	                    ? rs.getDate("ymd").toLocalDate()
	                    : LocalDate.now();

	            int eventsCount = rs.getInt("events_count");
	            int quizSubmits = rs.getInt("quiz_submits");
	            int wrongReviews = rs.getInt("wrong_reviews");

	            Timestamp ts = rs.getTimestamp("last_event_at");
	            LocalDateTime lastEventAt = (ts != null) ? ts.toLocalDateTime() : null;

	            // 오늘 이벤트가 0이면 "없다"
	            if (eventsCount <= 0) return null;

	            return new TodayRow(ymd, eventsCount, quizSubmits, wrongReviews, lastEventAt);
	        }, userId);

	        return Optional.ofNullable(row);

	    } catch (EmptyResultDataAccessException e) {
	        return Optional.empty();
	    }
	}

	private Optional<TodayRow> findTodayFromDailyLearning(long userId) {

	    String sql =
	        "SELECT ymd, events_count, quiz_submits, wrong_reviews, last_event_at " +
	        "FROM daily_learning " +
	        "WHERE user_id = ? AND ymd = CURDATE()";

	    try {
	        TodayRow row = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {

	            LocalDate ymd = rs.getDate("ymd").toLocalDate();
	            int eventsCount = rs.getInt("events_count");
	            int quizSubmits = rs.getInt("quiz_submits");
	            int wrongReviews = rs.getInt("wrong_reviews");

	            Timestamp ts = rs.getTimestamp("last_event_at");
	            LocalDateTime lastEventAt = (ts != null) ? ts.toLocalDateTime() : null;

	            return new TodayRow(ymd, eventsCount, quizSubmits, wrongReviews, lastEventAt);
	        }, userId);

	        return Optional.ofNullable(row);

	    } catch (EmptyResultDataAccessException e) {
	        return Optional.empty();
	    }
	}
	
	public int countEventsToday(long userId, LocalDate ymd) {
	    // [정공법] 날짜 = range 쿼리 (인덱스 타기 쉬움)
	    LocalDateTime start = ymd.atStartOfDay();
	    LocalDateTime end = ymd.plusDays(1).atStartOfDay();

	    String sql = """
	        SELECT COUNT(*)
	        FROM study_events
	        WHERE user_id = ?
	          AND created_at >= ?
	          AND created_at < ?
	    """;
	    Integer cnt = jdbcTemplate.queryForObject(sql, Integer.class,
	            userId,
	            Timestamp.valueOf(start),
	            Timestamp.valueOf(end)
	    );
	    return (cnt == null) ? 0 : cnt;
	}

	public Timestamp lastEventAtToday(long userId, LocalDate ymd) {
	    LocalDateTime start = ymd.atStartOfDay();
	    LocalDateTime end = ymd.plusDays(1).atStartOfDay();

	    String sql = """
	        SELECT MAX(created_at)
	        FROM study_events
	        WHERE user_id = ?
	          AND created_at >= ?
	          AND created_at < ?
	    """;
	    return jdbcTemplate.queryForObject(sql, Timestamp.class,
	            userId,
	            Timestamp.valueOf(start),
	            Timestamp.valueOf(end)
	    );
	}
	
	public int calcStreakDays(long userId, LocalDate today) {
	    int streak = 0;
	    LocalDate cursor = today;

	    String sql = """
	        SELECT events_count
	        FROM daily_learning
	        WHERE user_id = ?
	          AND ymd = ?
	    """;

	    while (true) {
	        Integer cnt;
	        try {
	            cnt = jdbcTemplate.queryForObject(
	                sql,
	                Integer.class,
	                userId,
	                java.sql.Date.valueOf(cursor)
	            );
	        } catch (EmptyResultDataAccessException e) {
	            cnt = null;
	        }

	        if (cnt == null || cnt <= 0) break;

	        streak++;
	        cursor = cursor.minusDays(1);
	    }

	    return streak;
	}
	
	public Timestamp lastEventAtAll(long userId) {
	    String sql = """
	        SELECT MAX(created_at)
	        FROM study_events
	        WHERE user_id = ?
	    """;
	    return jdbcTemplate.queryForObject(sql, Timestamp.class, userId);
	}

	public int countEventsRecentDays(long userId, int days) {
	    String sql = """
	        SELECT COUNT(*)
	        FROM study_events
	        WHERE user_id = ?
	          AND created_at >= DATE_SUB(NOW(), INTERVAL ? DAY)
	    """;
	    Integer cnt = jdbcTemplate.queryForObject(sql, Integer.class, userId, days);
	    return (cnt == null) ? 0 : cnt;
	}
	
	public int daysSinceLastEvent(long userId) {
	    String sql = """
	        SELECT COALESCE(TIMESTAMPDIFF(DAY, MAX(created_at), NOW()), 999)
	        FROM study_events
	        WHERE user_id = ?
	    """;
	    Integer d = jdbcTemplate.queryForObject(sql, Integer.class, userId);
	    return (d == null) ? 999 : d;
	}

	public int countEventsRecent3d(long userId) {
	    String sql = """
	        SELECT COUNT(*)
	        FROM study_events
	        WHERE user_id = ?
	          AND created_at >= DATE_SUB(NOW(), INTERVAL 3 DAY)
	    """;
	    Integer cnt = jdbcTemplate.queryForObject(sql, Integer.class, userId);
	    return (cnt == null) ? 0 : cnt;
	}
}