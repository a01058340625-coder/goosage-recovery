// src/main/java/com/goosage/dto/StatsOverviewResponse.java
package com.goosage.dto;

import java.time.LocalDateTime;
import java.util.List;

public record StatsOverviewResponse(
        long userId,
        long totalAttempts,
        double avgScorePercent,
        long todayAttempts,
        double todayAvgScorePercent,
        List<RecentAttempt> recentAttempts,
        List<WrongTopDetail> wrongTopKnowledge
) {
    public record RecentAttempt(long resultId, long knowledgeId, int scorePercent, LocalDateTime createdAt) {}

    // ✅ DAO가 쓰는 “원본 집계”(필요하면 유지)
    public record WrongTop(long knowledgeId, long wrongCount) {}

    // ✅ v0.8.3 응답용(제목 포함)
    public record WrongTopDetail(long knowledgeId, String title, long wrongCount) {}
}
