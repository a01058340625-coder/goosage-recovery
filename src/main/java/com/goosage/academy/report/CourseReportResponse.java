package com.goosage.academy.report;

import java.time.LocalDateTime;
import java.util.List;

public record CourseReportResponse(
        long courseId,
        int totalItems,
        int enrolledUsers,
        int activeUsers,
        int completedUsers,
        int avgCompletionPercent,
        LocalDateTime lastActivityAt,
        List<StuckItem> topStuckItems,
        List<TopWrongKnowledge> topWrongKnowledges
) {
    public record StuckItem(long itemId, long knowledgeId, int stuckUsers) {}
    public record TopWrongKnowledge(long knowledgeId, int wrongCount) {}
}

