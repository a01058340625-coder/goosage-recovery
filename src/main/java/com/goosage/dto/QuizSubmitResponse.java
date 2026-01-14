package com.goosage.dto;

import java.util.ArrayList;
import java.util.List;

public class QuizSubmitResponse {

    private long knowledgeId;
    private int total;
    private int correctCount;
    private List<QuizResultItem> results = new ArrayList<>();

    public QuizSubmitResponse() {}

    public QuizSubmitResponse(long knowledgeId, int total, int correctCount, List<QuizResultItem> results) {
        this.knowledgeId = knowledgeId;
        this.total = total;
        this.correctCount = correctCount;
        this.results = results;
    }

    public long getKnowledgeId() {
        return knowledgeId;
    }

    public void setKnowledgeId(long knowledgeId) {
        this.knowledgeId = knowledgeId;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
    }

    public List<QuizResultItem> getResults() {
        return results;
    }

    public void setResults(List<QuizResultItem> results) {
        this.results = results;
    }
}
