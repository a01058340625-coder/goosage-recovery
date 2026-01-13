package com.goosage.service;

import org.springframework.stereotype.Service;

import com.goosage.dto.KnowledgeDto;
import com.goosage.dto.TemplateDto;
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
                .orElseGet(() -> {
                    String result = knowledgeTemplateService.toSummaryV2(knowledge);
                    return templateRepository.save(new TemplateDto(knowledge.getId(), type, result));
                });
    }

    public TemplateDto getOrCreateQuizV2(KnowledgeDto knowledge) {
        final String type = "QUIZ_V2";

        return templateRepository.findByKnowledgeIdAndTemplateType(knowledge.getId(), type)
                .orElseGet(() -> {
                    String result = knowledgeTemplateService.toQuizV2Text(knowledge);
                    return templateRepository.save(new TemplateDto(knowledge.getId(), type, result));
                });
    }
}
