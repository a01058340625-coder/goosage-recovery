package com.goosage.repository;

import com.goosage.dto.KnowledgeDto;
import java.util.List;
import java.util.Optional;

public interface KnowledgeRepository {

    List<KnowledgeDto> findAll();

    KnowledgeDto save(KnowledgeDto knowledge);

    // ✅ 단건 조회는 이걸로만 제공(너 프로젝트 구조상)
    Optional<KnowledgeDto> findBySourceAndSourceId(String source, long sourceId);
}
