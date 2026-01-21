package com.goosage.academy.progress.dto;

import java.util.List;


public record ProgressSummary(
    long courseId,
    int totalCount,
    int doneCount,
    int percent,
    List<ProgressItem> items
) {
    public record ProgressItem(long itemId, long knowledgeId, boolean done) {}
}