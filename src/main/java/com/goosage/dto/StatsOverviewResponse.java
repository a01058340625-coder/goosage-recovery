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
        List<WrongTopDetail> wrongTopKnowledge   // ✅ 여기 타입 변경
) {
    public record RecentAttempt(long resultId, long knowledgeId, int scorePercent, LocalDateTime createdAt) {}

    // ✅ DAO가 쓰는 “원본 집계”
    public record WrongTop(long knowledgeId, long wrongCount) {}

    // ✅ v0.8.2 응답용(제목 포함)
    public record WrongTopDetail(long knowledgeId, String title, long wrongCount) {}
}
