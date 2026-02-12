package com.goosage.domain.knowledge;

import java.util.List;
import java.util.Optional;

import com.goosage.dto.KnowledgeDto;

public interface KnowledgeCrudPort {
    Optional<KnowledgeDto> findById(long id);
    List<KnowledgeDto> findAll();

    Optional<KnowledgeDto> findBySourceAndSourceId(String source, Long sourceId);

    KnowledgeDto save(KnowledgeDto req);
}
