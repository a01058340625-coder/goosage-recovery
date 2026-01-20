package com.goosage.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.goosage.dto.KnowledgeDto;

@Repository
@Primary


public class JdbcKnowledgeRepository implements KnowledgeRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcKnowledgeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<KnowledgeDto> rowMapper = (rs, n) -> {
        KnowledgeDto dto = new KnowledgeDto();
        dto.setId(rs.getLong("id"));
        dto.setSource(rs.getString("source"));
        dto.setSourceId(rs.getLong("source_id"));
        dto.setTitle(rs.getString("title"));
        dto.setContent(rs.getString("content"));
        // created_at, tags, summary 같은 게 있으면 여기서 추가로 매핑
        return dto;
    };

    @Override
    public KnowledgeDto save(KnowledgeDto knowledge) {
        if (knowledge.getId() != null) return knowledge;

        String sql = """
            INSERT INTO knowledge (type, source, source_id, title, content, tags)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        // ✅ 여기서 값을 확정
        final String tagsCsv =
                (knowledge.getTags() == null || knowledge.getTags().isEmpty())
                        ? null
                        : String.join(",", knowledge.getTags());

        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps =
                    con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, knowledge.getType());
            ps.setString(2, knowledge.getSource());
            ps.setLong(3, knowledge.getSourceId());
            ps.setString(4, knowledge.getTitle());
            ps.setString(5, knowledge.getContent());
            ps.setString(6, tagsCsv);   // ✅ 이제 OK

            return ps;
        }, kh);

        Number key = kh.getKey();
        if (key != null) knowledge.setId(key.longValue());
        return knowledge;
    }
    
    @Override
    public List<KnowledgeDto> findAll() {
        String sql = """
            SELECT id, type, source, source_id, title, content, tags
            FROM knowledge
            ORDER BY id DESC
        """;

        return jdbcTemplate.query(sql, (rs, n) -> {
            KnowledgeDto dto = new KnowledgeDto();
            dto.setId(rs.getLong("id"));
            dto.setType(rs.getString("type"));
            dto.setSource(rs.getString("source"));
            dto.setSourceId(rs.getLong("source_id"));
            dto.setTitle(rs.getString("title"));
            dto.setContent(rs.getString("content"));

            // tags: varchar에 CSV로 저장 중이면 -> List<String>로 복원
            String tagsCsv = rs.getString("tags");
            if (tagsCsv != null && !tagsCsv.isBlank()) {
                dto.setTags(List.of(tagsCsv.split(",")));
            } else {
                dto.setTags(List.of());
            }

            return dto;
        });
    }




    @Override
    public Optional<KnowledgeDto> findBySourceAndSourceId(String source, long sourceId) {
        String sql = """
                SELECT id, source, source_id, title, content
                FROM knowledge
                WHERE source = ? AND source_id = ?
                LIMIT 1
                """;

        List<KnowledgeDto> list = jdbcTemplate.query(sql, rowMapper, source, sourceId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}
