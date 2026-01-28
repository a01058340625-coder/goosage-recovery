package com.goosage.controller;

import com.goosage.ai.AiTemplateService;
import com.goosage.dao.KnowledgeDao;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AiTestController {

    private final KnowledgeDao knowledgeDao;
    private final AiTemplateService aiTemplateService;

    public AiTestController(KnowledgeDao knowledgeDao,
                            AiTemplateService aiTemplateService) {
        this.knowledgeDao = knowledgeDao;
        this.aiTemplateService = aiTemplateService;
    }

    @GetMapping("/ai/test/summary/{knowledgeId}")
    public String testSummary(@PathVariable long knowledgeId) {
        String content = knowledgeDao.findContentById(knowledgeId);
        return aiTemplateService.generateSummary(content);
    }
}
