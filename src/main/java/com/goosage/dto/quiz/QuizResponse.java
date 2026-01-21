package com.goosage.dto.quiz;

import java.util.List;

public class QuizResponse {

    private Long knowledgeId;
    private String templateType;
    private List<String> questions;

    public QuizResponse(Long knowledgeId, String templateType, List<String> questions) {
        this.knowledgeId = knowledgeId;
        this.templateType = templateType;
        this.questions = questions;
    }

    public Long getKnowledgeId() { return knowledgeId; }
    public String getTemplateType() { return templateType; }
    public List<String> getQuestions() { return questions; }
}
