package com.goosage.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.goosage.dto.template.TemplateDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class TemplateDao implements TemplateRepository {

    private static final Logger log =
        LoggerFactory.getLogger(TemplateDao.class);


    private final JdbcTemplate jdbcTemplate;
    private TemplateDto mapRow(ResultSet rs) throws SQLException {
        TemplateDto t = new TemplateDto();
        t.setId(rs.getLong("id"));
        t.setKnowledgeId(rs.getLong("knowledge_id"));
        t.setTemplateType(rs.getString("template_type"));
        t.setResultText(rs.getString("result_text"));
        t.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return t;
    }

    public TemplateDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<TemplateDto> findByKnowledgeIdAndTemplateType(long knowledgeId, String templateType) {

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


    @Override
    public TemplateDto save(TemplateDto template) {
        log.info("TEMPLATE SAVE CALLED: knowledgeId={}, type={}",
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

        log.info("TEMPLATE INSERT updated={}", updated); // 보통 1

        return template;
    }


}
