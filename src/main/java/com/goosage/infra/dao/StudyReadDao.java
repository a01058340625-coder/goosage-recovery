package com.goosage.infra.dao;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StudyReadDao {

    private final JdbcTemplate jdbcTemplate;

    public StudyReadDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<TodayRowRecord> findToday(long userId) {

    	String sql =
    		    "SELECT " +
    		    "  DATE(MAX(created_at)) AS ymd, " +
    		    "  COUNT(*) AS events_count, " +
    		    "  SUM(CASE WHEN type = 'QUIZ_SUBMIT' THEN 1 ELSE 0 END) AS quiz_submits, " +
    		    "  SUM(CASE WHEN type = 'REVIEW_WRONG' THEN 1 ELSE 0 END) AS wrong_reviews, " +
    		    "  SUM(CASE WHEN type = 'WRONG_REVIEW_DONE' THEN 1 ELSE 0 END) AS wrong_review_done_count " +
    		    "FROM study_events " +
    		    "WHERE user_id = ? " +
    		    "  AND DATE(created_at) = CURDATE() " +
    		    "HAVING COUNT(*) > 0";

        try {
            TodayRowRecord row = jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> new TodayRowRecord(
                	    rs.getDate("ymd").toLocalDate(),
                	    rs.getInt("events_count"),
                	    rs.getInt("quiz_submits"),
                	    rs.getInt("wrong_reviews"),
                	    rs.getInt("wrong_review_done_count")   // 🔥 추가
                	),
                userId
            );

            return Optional.ofNullable(row);

        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<LocalDateTime> lastEventAtAll(long userId) {
        String sql = "SELECT MAX(created_at) FROM study_events WHERE user_id = ?";
        Timestamp ts = jdbcTemplate.queryForObject(sql, Timestamp.class, userId);
        return (ts == null) ? Optional.empty() : Optional.of(ts.toLocalDateTime());
    }

    public int calcStreakDays(long userId, LocalDate today) {

        String sql =
            "SELECT DISTINCT DATE(created_at) AS ymd " +
            "FROM study_events " +
            "WHERE user_id = ? " +
            "ORDER BY ymd DESC";

        List<LocalDate> days = jdbcTemplate.query(
            sql,
            (rs, rowNum) -> rs.getDate("ymd").toLocalDate(),
            userId
        );

        if (days.isEmpty()) return 0;

        // 기준 anchor:
        // - 오늘 학습했으면 today부터
        // - 아니면 yesterday부터 (오늘 비어있어도 streak 유지되게)
        LocalDate anchor = today;
        if (!days.contains(today)) {
            anchor = today.minusDays(1);
        }

        int streak = 0;
        LocalDate cursor = anchor;

        // days는 내림차순. cursor부터 하루씩 감소하며 존재 여부 체크
        // (데이터량 적을 때는 contains로 충분. 커지면 Set으로 바꾸면 됨)
        while (days.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }

        return streak;
    }
    
    public int todayEventCountFromEvents(long userId, LocalDate today) {

        // MySQL: 오늘 00:00:00 ~ 내일 00:00:00 미만
        String sql =
            "SELECT COUNT(*) " +
            "FROM study_events " +
            "WHERE user_id = ? " +
            "  AND created_at >= ? " +
            "  AND created_at < ?";

        Timestamp from = Timestamp.valueOf(today.atStartOfDay());
        Timestamp to   = Timestamp.valueOf(today.plusDays(1).atStartOfDay());

        Integer cnt = jdbcTemplate.queryForObject(sql, Integer.class, userId, from, to);
        return (cnt == null) ? 0 : cnt;
    }
    
    public int recentEventCount3d(long userId, LocalDate today) {

        // 최근 3일(오늘 포함) = [today-2 00:00:00, tomorrow 00:00:00)
        String sql =
            "SELECT COUNT(*) " +
            "FROM study_events " +
            "WHERE user_id = ? " +
            "  AND created_at >= ? " +
            "  AND created_at < ?";

        Timestamp from = Timestamp.valueOf(today.minusDays(2).atStartOfDay());
        Timestamp to   = Timestamp.valueOf(today.plusDays(1).atStartOfDay());

        Integer cnt = jdbcTemplate.queryForObject(sql, Integer.class, userId, from, to);
        return (cnt == null) ? 0 : cnt;
    }
    
    public int recentWrong3d(long userId, LocalDate today) {

    	String sql =
    		    "SELECT COUNT(*) " +
    		    "FROM study_events " +
    		    "WHERE user_id = ? " +
    		    "  AND type = 'REVIEW_WRONG' " +
    		    "  AND created_at >= ? " +
    		    "  AND created_at < ?";

        Timestamp from = Timestamp.valueOf(today.minusDays(2).atStartOfDay());
        Timestamp to   = Timestamp.valueOf(today.plusDays(1).atStartOfDay());

        Integer cnt = jdbcTemplate.queryForObject(sql, Integer.class, userId, from, to);
        return (cnt == null) ? 0 : cnt;
    }

    public int recentWrongDone3d(long userId, LocalDate today) {

        String sql =
            "SELECT COUNT(*) " +
            "FROM study_events " +
            "WHERE user_id = ? " +
            "  AND type = 'WRONG_REVIEW_DONE' " +
            "  AND created_at >= ? " +
            "  AND created_at < ?";

        Timestamp from = Timestamp.valueOf(today.minusDays(2).atStartOfDay());
        Timestamp to   = Timestamp.valueOf(today.plusDays(1).atStartOfDay());

        Integer cnt = jdbcTemplate.queryForObject(sql, Integer.class, userId, from, to);
        return (cnt == null) ? 0 : cnt;
    }
    
    public int todayWrongFromEvents(long userId, LocalDate today) {

        String sql =
            "SELECT COUNT(*) " +
            "FROM study_events " +
            "WHERE user_id = ? " +
            "  AND type = 'REVIEW_WRONG' " +
            "  AND created_at >= ? " +
            "  AND created_at < ?";

        Timestamp from = Timestamp.valueOf(today.atStartOfDay());
        Timestamp to   = Timestamp.valueOf(today.plusDays(1).atStartOfDay());

        Integer cnt = jdbcTemplate.queryForObject(sql, Integer.class, userId, from, to);
        return (cnt == null) ? 0 : cnt;
    }
    
    public int todayWrongDoneFromEvents(long userId, LocalDate today) {

        String sql =
            "SELECT COUNT(*) " +
            "FROM study_events " +
            "WHERE user_id = ? " +
            "  AND type = 'WRONG_REVIEW_DONE' " +
            "  AND created_at >= ? " +
            "  AND created_at < ?";

        Timestamp from = Timestamp.valueOf(today.atStartOfDay());
        Timestamp to   = Timestamp.valueOf(today.plusDays(1).atStartOfDay());

        Integer cnt = jdbcTemplate.queryForObject(sql, Integer.class, userId, from, to);
        return (cnt == null) ? 0 : cnt;
    }
    
    public int todayQuizFromEvents(long userId, LocalDate today) {

        String sql =
            "SELECT COUNT(*) " +
            "FROM study_events " +
            "WHERE user_id = ? " +
            "  AND type = 'QUIZ_SUBMIT' " +
            "  AND created_at >= ? " +
            "  AND created_at < ?";

        Timestamp from = Timestamp.valueOf(today.atStartOfDay());
        Timestamp to   = Timestamp.valueOf(today.plusDays(1).atStartOfDay());

        Integer cnt = jdbcTemplate.queryForObject(sql, Integer.class, userId, from, to);
        return (cnt == null) ? 0 : cnt;
    }

}
