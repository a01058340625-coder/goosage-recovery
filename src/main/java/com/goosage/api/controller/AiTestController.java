package com.goosage.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.app.AiTestService;

@RestController
public class AiTestController {

    private final AiTestService aiTestService;

    public AiTestController(AiTestService aiTestService) {
        this.aiTestService = aiTestService;
    }

    @GetMapping("/ai/test/summary/{knowledgeId}")
    public String testSummary(@PathVariable long knowledgeId) {
        return aiTestService.summary(knowledgeId);
    }
}
