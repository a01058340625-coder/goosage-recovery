package com.goosage.domain.template;

import java.util.Optional;

import com.goosage.entity.Template;

public interface TemplatePort {
    Optional<Template> findByKnowledgeIdAndTemplateType(long knowledgeId, String templateType);
    Template save(Template t);
}
