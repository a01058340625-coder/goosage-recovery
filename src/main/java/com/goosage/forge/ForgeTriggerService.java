package com.goosage.forge;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.goosage.dto.template.TemplateDto;
import com.goosage.repository.TemplateRepository;

@Service
public class ForgeTriggerService {

    private final TemplateRepository templateRepository;

    public ForgeTriggerService(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    public ForgePrepareResult prepare(long knowledgeId, String templateType){
        try {
            // 1) 있으면 REUSE
            Optional<TemplateDto> existing =
                    templateRepository.findByKnowledgeIdAndTemplateType(knowledgeId, templateType);

            if (existing.isPresent()) {
                return ForgePrepareResult.reused(existing.get().getId());
            }

            // 2) 없으면 CREATED
            // TemplateDto에는 userId가 없으니 knowledgeId/templateType/resultText만 채운다.
            TemplateDto created = new TemplateDto(knowledgeId, templateType, "");
            TemplateDto saved = templateRepository.save(created);

            return ForgePrepareResult.created(saved.getId());

        } catch (Exception e) {
            return ForgePrepareResult.failed("forge failed: " + e.getClass().getSimpleName());
        }
    }
}
