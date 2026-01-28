package com.goosage.ai;

import org.springframework.stereotype.Service;

@Service
public class AiTemplateService {

    private final AiClient aiClient;
    private final AiPromptFactory promptFactory;

    public AiTemplateService(AiClient aiClient, AiPromptFactory promptFactory) {
        this.aiClient = aiClient;
        this.promptFactory = promptFactory;
    }

    public String generateSummary(String knowledgeContent) {
        String prompt = promptFactory.summaryPrompt(knowledgeContent);

        // 지금은 prompt를 직접 쓰진 않음 (개념만 유지)
        return aiClient.summarize(knowledgeContent);
    }
}
