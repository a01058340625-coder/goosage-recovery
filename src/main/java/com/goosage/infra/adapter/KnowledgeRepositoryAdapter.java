package com.goosage.infra.adapter;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.goosage.domain.knowledge.KnowledgePort;
import com.goosage.domain.knowledge.KnowledgeView;
import com.goosage.infra.repository.KnowledgeRepository;

@Component
public class KnowledgeRepositoryAdapter implements KnowledgePort {

    private final KnowledgeRepository knowledgeRepository;

    public KnowledgeRepositoryAdapter(KnowledgeRepository knowledgeRepository) {
        this.knowledgeRepository = knowledgeRepository;
    }

    @Override
    public Optional<KnowledgeView> findById(long id) {
        return knowledgeRepository.findById(id)
                .map(dto -> new KnowledgeView(dto.getId(), dto.getContent()));
    }
}
