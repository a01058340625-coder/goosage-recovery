package com.goosage.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.goosage.dto.KnowledgeDto;

@Repository
public class InMemoryKnowledgeRepository implements KnowledgeRepository {

    private final List<KnowledgeDto> store = new ArrayList<>();
    private long seq = 1L;

    @Override
    public List<KnowledgeDto> findAll() {
        return store;
    }

    @Override
    public KnowledgeDto save(KnowledgeDto knowledge) {
        if (knowledge.getId() == null) {
            knowledge.setId(seq++);
        }
        if (knowledge.getCreatedAt() == null) {
            knowledge.setCreatedAt(LocalDateTime.now());
        }
        store.add(knowledge);
        return knowledge;
    }

    @Override
    public Optional<KnowledgeDto> findBySourceAndSourceId(String source, long sourceId) {
        return store.stream()
                .filter(k ->
                        source != null &&
                        source.equals(k.getSource()) &&
                        k.getSourceId() != null &&
                        k.getSourceId() == sourceId
                )
                .findFirst();
    }
}
