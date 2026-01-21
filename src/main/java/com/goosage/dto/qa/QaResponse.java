package com.goosage.dto.qa;

import java.time.LocalDateTime;

import com.goosage.entity.QaEntity;

public class QaResponse {
    private Long id;
    private String question;
    private String answer;
    private String tags;
    private LocalDateTime createdAt;

    public static QaResponse from(QaEntity e) {
        QaResponse r = new QaResponse();
        r.id = e.getId();
        r.question = e.getQuestion();
        r.answer = e.getAnswer();
        r.tags = e.getTags();
        r.createdAt = e.getCreatedAt();
        return r;
    }

    public Long getId() { return id; }
    public String getQuestion() { return question; }
    public String getAnswer() { return answer; }
    public String getTags() { return tags; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
