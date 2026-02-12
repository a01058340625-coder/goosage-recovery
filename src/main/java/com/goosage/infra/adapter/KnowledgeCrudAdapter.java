package com.goosage.infra.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.goosage.domain.knowledge.KnowledgeCrudPort;
import com.goosage.dto.KnowledgeDto;
import com.goosage.infra.repository.KnowledgeRepository;

@Component
public class KnowledgeCrudAdapter implements KnowledgeCrudPort {

    private final KnowledgeRepository repository;

    public KnowledgeCrudAdapter(KnowledgeRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<KnowledgeDto> findById(long id) {
        return repository.findById(id);
    }

    @Override
    public List<KnowledgeDto> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<KnowledgeDto> findBySourceAndSourceId(String source, Long sourceId) {
        return repository.findBySourceAndSourceId(source, sourceId);
    }

    @Override
    public KnowledgeDto save(KnowledgeDto req) {
        return repository.save(req);
    }
}
