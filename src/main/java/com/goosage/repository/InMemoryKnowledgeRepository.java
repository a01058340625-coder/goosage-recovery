package com.goosage.repository;

import com.goosage.dto.KnowledgeDto;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
}
