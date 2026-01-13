package com.goosage.dto;

import java.time.LocalDateTime;

public class TemplateDto {
	private long id;
	private long knowledgeId;
	private String templateType;
	private String resultText;
	private LocalDateTime createdAt;
	
	public TemplateDto() {}


	public TemplateDto(long knowledgeId, String templateType, String resultText) {
		this.knowledgeId = knowledgeId;
		this.templateType = templateType;
		this.resultText = resultText;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getKnowledgeId() {
		return knowledgeId;
	}

	public void setKnowledgeId(long knowledgeId) {
		this.knowledgeId = knowledgeId;
	}

	public String getTemplateType() {
		return templateType;
	}

	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}

	public String getResultText() {
		return resultText;
	}

	public void setResultText(String resultText) {
		this.resultText = resultText;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	// getter/setter
}
