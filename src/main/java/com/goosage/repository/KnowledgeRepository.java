package com.goosage.repository;

import java.util.List;
import java.util.Optional;

import com.goosage.dto.KnowledgeDto;

public interface KnowledgeRepository {

    List<KnowledgeDto> findAll();

    KnowledgeDto save(KnowledgeDto knowledge);

    Optional<KnowledgeDto> findBySourceAndSourceId(String source, long sourceId);
}
