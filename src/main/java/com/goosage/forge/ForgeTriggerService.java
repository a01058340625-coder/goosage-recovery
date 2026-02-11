package com.goosage.forge;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.goosage.entity.Template;
import com.goosage.infra.repository.TemplateRepository;

@Service
public class ForgeTriggerService {

    private final TemplateRepository templateRepository;

    public ForgeTriggerService(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    public ForgePrepareResult prepare(long knowledgeId, String templateType){
        try {
            // 1) 있으면 REUSE
            Optional<Template> existing =
                    templateRepository.findByKnowledgeIdAndTemplateType((long) knowledgeId, templateType);

            if (existing.isPresent()) {
                return ForgePrepareResult.reused(existing.get().getId());
            }

            // 2) 없으면 CREATED
            Template created = new Template();
            created.setKnowledgeId(knowledgeId);
            created.setTemplateType(templateType);
            created.setResultText(""); // 초기값

            Template saved = templateRepository.save(created);

            return ForgePrepareResult.created(saved.getId());

        } catch (Exception e) {
            return ForgePrepareResult.failed("forge failed: " + e.getClass().getSimpleName());
        }
    }
}
