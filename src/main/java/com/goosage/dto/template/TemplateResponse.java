package com.goosage.dto.template;

public class TemplateResponse {

    private Long knowledgeId;
    private String templateType;   // "SUMMARY_V1"
    private String resultText;     // 완성 텍스트

    public TemplateResponse() {}

    public TemplateResponse(Long knowledgeId, String templateType, String resultText) {
        this.knowledgeId = knowledgeId;
        this.templateType = templateType;
        this.resultText = resultText;
    }

    public Long getKnowledgeId() { return knowledgeId; }
    public void setKnowledgeId(Long knowledgeId) { this.knowledgeId = knowledgeId; }

    public String getTemplateType() { return templateType; }
    public void setTemplateType(String templateType) { this.templateType = templateType; }

    public String getResultText() { return resultText; }
    public void setResultText(String resultText) { this.resultText = resultText; }
}
