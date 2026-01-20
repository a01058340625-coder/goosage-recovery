package com.goosage.academy.workspace;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class AcademyWorkspaceDao {

    private final JdbcTemplate jdbcTemplate;

    public AcademyWorkspaceDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long insertWorkspace(String name, String planType) {
        String sql = "INSERT INTO academy_workspace(name, plan_type, created_at) VALUES(?,?,NOW())";
        KeyHolder kh = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, planType);
            return ps;
        }, kh);

        Number key = kh.getKey();
        if (key == null) throw new RuntimeException("workspace id 생성 실패");
        return key.longValue();
    }

    public void insertMember(long workspaceId, long userId, String role) {
        String sql = """
            INSERT INTO academy_member(workspace_id, user_id, role, joined_at)
            VALUES(?,?,?,NOW())
            """;
        jdbcTemplate.update(sql, workspaceId, userId, role);
    }

    public List<WorkspaceResponse> findMyWorkspaces(long userId) {
        String sql = """
            SELECT w.id, w.name, w.plan_type, w.created_at
            FROM academy_workspace w
            JOIN academy_member m ON m.workspace_id = w.id
            WHERE m.user_id = ?
            ORDER BY w.id DESC
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new WorkspaceResponse(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("plan_type"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ), userId);
    }
}
