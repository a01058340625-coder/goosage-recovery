package com.goosage.repository;

import com.goosage.dto.KnowledgeDto;

import java.util.List;

public interface KnowledgeRepository {

    List<KnowledgeDto> findAll();

    KnowledgeDto save(KnowledgeDto knowledge);
}
