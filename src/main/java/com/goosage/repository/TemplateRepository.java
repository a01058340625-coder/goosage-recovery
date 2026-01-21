package com.goosage.repository;

import java.util.Optional;

import com.goosage.dto.template.TemplateDto;

public interface TemplateRepository {

    Optional<TemplateDto> findByKnowledgeIdAndTemplateType(long knowledgeId, String templateType);

    TemplateDto save(TemplateDto template);
}
