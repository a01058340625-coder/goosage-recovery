package com.goosage.infra.adapter;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.goosage.domain.template.TemplatePort;
import com.goosage.entity.Template;
import com.goosage.infra.repository.TemplateRepository;

@Repository
public class TemplateRepositoryAdapter implements TemplatePort {

    private final TemplateRepository templateRepository;

    public TemplateRepositoryAdapter(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Override
    public Optional<Template> findByKnowledgeIdAndTemplateType(long knowledgeId, String templateType) {
        return templateRepository.findByKnowledgeIdAndTemplateType(knowledgeId, templateType);
    }

    @Override
    public Template save(Template t) {
        return templateRepository.save(t);
    }
}
