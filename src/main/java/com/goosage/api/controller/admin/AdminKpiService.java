package com.goosage.api.controller.admin;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AdminKpiService {

    private final JdbcTemplate jdbc;

    public AdminKpiService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * 오늘(서버 로컬 기준) 이벤트 발생한 사용자 id 목록
     * - created_at >= CURDATE() : MySQL 서버 날짜 기준
     */
    public List<Long> findActiveUserIdsToday() {
        String sql = """
            SELECT DISTINCT user_id
            FROM study_events
            WHERE created_at >= CURDATE()
            ORDER BY user_id
        """;

        return jdbc.query(sql, (rs, rowNum) -> rs.getLong("user_id"));
    }
}