package com.goosage.dao;

import java.sql.*;

import javax.sql.DataSource;

public class KnowledgeDao {

    private final DataSource dataSource;

    public KnowledgeDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @return
     *  - 1  : 변환 성공
     *  - 0  : 이미 변환됨
     *  - -1 : QA 없음
     *  - -2 : answer 없음
     */
    public int insertFromQa(long qaId) {
        try (Connection conn = dataSource.getConnection()) {

            // 1) QA 존재 + answer 확인
            String qaSql = """
                SELECT question, answer, tags
                FROM qa
                WHERE id = ?
            """;

            String question;
            String answer;
            String tags;

            try (PreparedStatement ps = conn.prepareStatement(qaSql)) {
                ps.setLong(1, qaId);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) return -1; // QA 없음

                question = rs.getString("question");
                answer = rs.getString("answer");
                tags = rs.getString("tags");

                if (answer == null) return -2; // 답변 없음
            }

            // 2) 이미 변환됐는지 체크
            String existsSql = """
                SELECT id
                FROM knowledge
                WHERE source='qa' AND source_id=?
                LIMIT 1
            """;

            try (PreparedStatement ps = conn.prepareStatement(existsSql)) {
                ps.setLong(1, qaId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) return 0; // 이미 변환됨
            }

            // 3) INSERT
            String insertSql = """
                INSERT INTO knowledge (type, title, content, source, source_id, tags)
                VALUES ('QNA', ?, ?, 'qa', ?, ?)
            """;

            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setString(1, question);
                ps.setString(2, answer);
                ps.setLong(3, qaId);
                ps.setString(4, tags);
                ps.executeUpdate();
            }

            return 1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
