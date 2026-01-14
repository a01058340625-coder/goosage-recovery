package com.goosage.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class QuizResultResponse {

    private long id;
    private int totalCount;
    private int correctCount;
    private int scorePercent;
    private List<Map<String, Object>> details;
    private LocalDateTime createdAt;

    public QuizResultResponse(long id,
                              int totalCount,
                              int correctCount,
                              int scorePercent,
                              List<Map<String, Object>> details,
                              LocalDateTime createdAt) {
        this.id = id;
        this.totalCount = totalCount;
        this.correctCount = correctCount;
        this.scorePercent = scorePercent;
        this.details = details;
        this.createdAt = createdAt;
    }

    public long getId() { return id; }
    public int getTotalCount() { return totalCount; }
    public int getCorrectCount() { return correctCount; }
    public int getScorePercent() { return scorePercent; }
    public List<Map<String, Object>> getDetails() { return details; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
