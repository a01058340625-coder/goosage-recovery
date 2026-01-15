package com.goosage.dto;

import java.time.LocalDateTime;

public class QuizItemDto {

    private long id;
    private long knowledgeId;
    private int no;
    private String question;
    private String expected;
    private LocalDateTime createdAt;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getKnowledgeId() { return knowledgeId; }
    public void setKnowledgeId(long knowledgeId) { this.knowledgeId = knowledgeId; }

    public int getNo() { return no; }
    public void setNo(int no) { this.no = no; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getExpected() { return expected; }
    public void setExpected(String expected) { this.expected = expected; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
