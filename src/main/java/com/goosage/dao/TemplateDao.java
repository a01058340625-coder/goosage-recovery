package com.goosage.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
// import org.springframework.stereotype.Repository;  // ✅ 지금 단계에서는 DAO 퇴장(빈 등록 차단)

import com.goosage.dto.template.TemplateDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// ✅ FM 잠금: JPA TemplateRepository로 통일.
// ✅ TemplateDao(JdbcTemplate)는 "특수쿼리/성능" 필요할 때만 다시 활성화.
// @Repository
public class TemplateDao {

    private static final Logger log =
        LoggerFactory.getLogger(TemplateDao.class);

    private final JdbcTemplate jdbcTemplate;

    public TemplateDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private TemplateDto mapRow(ResultSet rs) throws SQLException {
        TemplateDto t = new TemplateDto();
        t.setId(rs.getLong("id"));
        t.setKnowledgeId(rs.getLong("knowledge_id"));
        t.setTemplateType(rs.getString("template_type"));
        t.setResultText(rs.getString("result_text"));
        t.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return t;
    }

    // ✅ 주의: 이 DAO 메서드들은 현재 "사용하지 않는" 상태가 정공법.
    // (필요하면 나중에 활성화해서 특수 케이스에서만 사용)

    public Optional<TemplateDto> findOneDtoByKnowledgeIdAndType(long knowledgeId, String templateType) {
        String sql = """
            SELECT id, knowledge_id, template_type, result_text, created_at
            FROM templates
            WHERE knowledge_id = ?
              AND template_type = ?
            LIMIT 1
        """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> mapRow(rs),
                knowledgeId, templateType
        ).stream().findFirst();
    }

    public TemplateDto insertAndReturnId(TemplateDto template) {
        log.info("TEMPLATE DAO INSERT CALLED: knowledgeId={}, type={}",
            template.getKnowledgeId(), template.getTemplateType());

        String sql = """
            INSERT INTO templates (knowledge_id, template_type, result_text, created_at)
            VALUES (?, ?, ?, NOW())
        """;

        int updated = jdbcTemplate.update(
            sql,
            template.getKnowledgeId(),
            template.getTemplateType(),
            template.getResultText()
        );

        log.info("TEMPLATE DAO INSERT updated={}", updated);

        Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        template.setId(id != null ? id : 0L);

        log.info("TEMPLATE DAO CREATED id={}", template.getId());

        return template;
    }

    public boolean existsByKnowledgeIdAndType(long knowledgeId, String templateType) {
        Integer exists = jdbcTemplate.queryForObject(
            "SELECT EXISTS(SELECT 1 FROM templates WHERE knowledge_id=? AND template_type=?)",
            Integer.class,
            knowledgeId, templateType
        );
        return exists != null && exists == 1;
    }

    public void insertTemplate(long knowledgeId, String templateType, String resultText) {
        jdbcTemplate.update(
                "INSERT INTO templates (knowledge_id, template_type, result_text) VALUES (?,?,?)",
                knowledgeId, templateType, resultText
        );
    }
}
