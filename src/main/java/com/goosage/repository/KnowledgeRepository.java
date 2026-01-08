package com.goosage.repository;

import com.goosage.dto.KnowledgeDto;
import java.util.List;
import java.util.Optional;

public interface KnowledgeRepository {

    List<KnowledgeDto> findAll();

    KnowledgeDto save(KnowledgeDto knowledge);

    // ✅ 추가 (v0.2 핵심)
    Optional<KnowledgeDto> findBySourceAndSourceId(String source, long sourceId);
}
