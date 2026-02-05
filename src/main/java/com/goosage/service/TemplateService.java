package com.goosage.service;

import org.springframework.stereotype.Service;

import com.goosage.dto.KnowledgeDto;
import com.goosage.dto.template.TemplateDto;
import com.goosage.entity.Template;
import com.goosage.repository.TemplateRepository;

@Service
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final KnowledgeTemplateService knowledgeTemplateService;

    public TemplateService(TemplateRepository templateRepository,
                           KnowledgeTemplateService knowledgeTemplateService) {
        this.templateRepository = templateRepository;
        this.knowledgeTemplateService = knowledgeTemplateService;
    }

    public TemplateDto getOrCreateSummaryV2(KnowledgeDto knowledge) {
        final String type = "SUMMARY_V2";

        return templateRepository.findByKnowledgeIdAndTemplateType(knowledge.getId(), type)
                .map(this::toDto)
                .orElseGet(() -> {
                    String result = knowledgeTemplateService.toSummaryV2(knowledge);

                    Template created = new Template();
                    created.setKnowledgeId(knowledge.getId());
                    created.setTemplateType(type);
                    created.setResultText(result);

                    Template saved = templateRepository.save(created);
                    return toDto(saved);
                });
    }

    public TemplateDto getOrCreateQuizV2(KnowledgeDto knowledge) {
        final String type = "QUIZ_V2";

        return templateRepository.findByKnowledgeIdAndTemplateType(knowledge.getId(), type)
                .map(this::toDto)
                .orElseGet(() -> {
                    String result = knowledgeTemplateService.toQuizV2Text(knowledge);

                    Template created = new Template();
                    created.setKnowledgeId(knowledge.getId());
                    created.setTemplateType(type);
                    created.setResultText(result);

                    Template saved = templateRepository.save(created);
                    return toDto(saved);
                });
    }

    private TemplateDto toDto(Template t) {
        TemplateDto dto = new TemplateDto(t.getKnowledgeId(), t.getTemplateType(), t.getResultText());
        dto.setId(t.getId() != null ? t.getId() : 0L);
        // createdAt/updatedAt이 TemplateDto에 있으면 여기서 추가로 set (없으면 생략)
        return dto;
    }
}
