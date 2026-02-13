package com.goosage.app;

import org.springframework.stereotype.Service;

import com.goosage.domain.template.TemplatePort;
import com.goosage.dto.KnowledgeDto;
import com.goosage.dto.template.TemplateDto;
import com.goosage.entity.Template;

@Service
public class TemplateService {

    private final TemplatePort templatePort;
    private final KnowledgeTemplateService knowledgeTemplateService;

    public TemplateService(TemplatePort templatePort,
                           KnowledgeTemplateService knowledgeTemplateService) {
        this.templatePort = templatePort;
        this.knowledgeTemplateService = knowledgeTemplateService;
    }

    public TemplateDto getOrCreateSummaryV2(KnowledgeDto knowledge) {
        final String type = "SUMMARY_V2";

        return templatePort.findByKnowledgeIdAndTemplateType(knowledge.getId(), type)
                .map(this::toDto)
                .orElseGet(() -> {
                    String result = knowledgeTemplateService.toSummaryV2(knowledge);

                    Template created = new Template();
                    created.setKnowledgeId(knowledge.getId());
                    created.setTemplateType(type);
                    created.setResultText(result);

                    Template saved = templatePort.save(created);
                    return toDto(saved);
                });
    }

    public TemplateDto getOrCreateQuizV2(KnowledgeDto knowledge) {
        final String type = "QUIZ_V2";

        return templatePort.findByKnowledgeIdAndTemplateType(knowledge.getId(), type)
                .map(this::toDto)
                .orElseGet(() -> {
                    String result = knowledgeTemplateService.toQuizV2Text(knowledge);

                    Template created = new Template();
                    created.setKnowledgeId(knowledge.getId());
                    created.setTemplateType(type);
                    created.setResultText(result);

                    Template saved = templatePort.save(created);
                    return toDto(saved);
                });
    }

    private TemplateDto toDto(Template t) {
        TemplateDto dto = new TemplateDto(t.getKnowledgeId(), t.getTemplateType(), t.getResultText());
        dto.setId(t.getId() != null ? t.getId() : 0L);
        return dto;
    }
}
