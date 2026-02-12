package com.goosage.app;

import org.springframework.stereotype.Service;

import com.goosage.ai.AiTemplateService;
import com.goosage.domain.knowledge.KnowledgePort;

@Service
public class AiTestService {

    private final KnowledgePort knowledgePort;
    private final AiTemplateService aiTemplateService;

    public AiTestService(KnowledgePort knowledgePort,
                         AiTemplateService aiTemplateService) {
        this.knowledgePort = knowledgePort;
        this.aiTemplateService = aiTemplateService;
    }

    public String summary(long knowledgeId) {

        var view = knowledgePort.findById(knowledgeId)
                .orElseThrow(() -> new IllegalArgumentException("knowledge not found: " + knowledgeId));

        String content = view.content() == null ? "" : view.content();

        return aiTemplateService.generateSummary(content);
    }
}
