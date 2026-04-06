package com.goosage.domain.recovery;

import java.time.LocalDate;

/**
 * Domain row (오늘 집계 결과)
 * - infra.dao.TodayRow 와 분리
 * - Port 계약에서 이 타입만 사용
 */
public record TodayRow(
        LocalDate ymd,
        int eventsCount,
        int quizSubmits,
        int wrongReviews,
        int wrongReviewDoneCount,
        Long recentKnowledgeId
) {}