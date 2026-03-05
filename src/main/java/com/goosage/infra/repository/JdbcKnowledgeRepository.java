package com.goosage.infra.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.goosage.common.TagsCsv;
import com.goosage.dto.KnowledgeDto;

@Repository
public class JdbcKnowledgeRepository implements KnowledgeRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcKnowledgeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ✅ 모든 조회에서 동일한 shape를 보장하는 단일 매퍼
    private final RowMapper<KnowledgeDto> rowMapper = (rs, n) -> {
        KnowledgeDto dto = new KnowledgeDto();
        dto.setId(rs.getLong("id"));
        dto.setType(rs.getString("type"));
        dto.setSource(rs.getString("source"));
        Long sid = rs.getObject("source_id", Long.class);
        dto.setSourceId(sid);

        dto.setTitle(rs.getString("title"));
        dto.setContent(rs.getString("content"));
        dto.setTags(TagsCsv.split(rs.getString("tags")));
        return dto;
    };

    @Override
    public KnowledgeDto save(KnowledgeDto knowledge) {
        // 정책을 명확히: 지금은 INSERT-ONLY로 보임
        if (knowledge.getId() != null) {
            // update를 지원하지 않을거면 조용히 리턴하지 말고 "명확히" 실패시키는 게 맞다
            throw new IllegalArgumentException("save() is insert-only. id must be null.");
        }

        String sql = """
            INSERT INTO knowledge (type, source, source_id, title, content, tags)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        final String tagsCsv = TagsCsv.join(knowledge.getTags());

        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, knowledge.getType());
            ps.setString(2, knowledge.getSource());
            ps.setObject(3, knowledge.getSourceId(), java.sql.Types.BIGINT);
            ps.setString(4, knowledge.getTitle());
            ps.setString(5, knowledge.getContent());
            ps.setString(6, tagsCsv);
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
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public Optional<KnowledgeDto> findBySourceAndSourceId(String source, long sourceId) {
        String sql = """
            SELECT id, type, source, source_id, title, content, tags
            FROM knowledge
            WHERE source = ? AND source_id = ?
            LIMIT 1
        """;

        List<KnowledgeDto> list = jdbcTemplate.query(sql, rowMapper, source, sourceId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
    
    @Override
    public Optional<KnowledgeDto> findById(long id) {
        String sql = """
            SELECT id, type, source, source_id, title, content, tags
            FROM knowledge
            WHERE id = ?
            LIMIT 1
        """;
        List<KnowledgeDto> list = jdbcTemplate.query(sql, rowMapper, id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

}
