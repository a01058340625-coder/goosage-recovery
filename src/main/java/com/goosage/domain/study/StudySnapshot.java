package com.goosage.domain.study;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * StudySnapshot = SSOT(단일 진실)
 * - Coach / Prediction / NextAction이 참고하는 읽기 전용 스냅샷
 * - Rule / Engine / Controller는 DB를 직접 보지 않고 Snapshot만 본다.
 */
public record StudySnapshot(
        LocalDate ymd,                 // 오늘 날짜(기준일)
        StudyState state,              // 집계 상태(events/quiz/wrong 등)
        boolean studiedToday,          // 오늘 학습 여부(오늘 이벤트 존재)
        int streakDays,                // 연속 학습일
        LocalDateTime lastEventAt,     // 전체 마지막 이벤트 시각
        int daysSinceLastEvent,        // 마지막 이벤트로부터 경과 일수
        int recentEventCount3d,        // 최근 3일 이벤트 수
        Long recentKnowledgeId         // 최근 지식/노트 ID
) {

    public double openRatio() {
        if (state == null || state.eventsCount() <= 0) return 0.0;
        return (double) state.justOpenCount() / state.eventsCount();
    }

    public double quizRatio() {
        if (state == null || state.eventsCount() <= 0) return 0.0;
        return (double) state.quizSubmits() / state.eventsCount();
    }

    public double wrongRatio() {
        if (state == null || state.eventsCount() <= 0) return 0.0;
        return (double) state.wrongReviews() / state.eventsCount();
    }

    public double wrongDoneRatio() {
        if (state == null || state.eventsCount() <= 0) return 0.0;
        return (double) state.wrongReviewDoneCount() / state.eventsCount();
    }

    public boolean hasWrongToReview() {
        return state != null && state.wrongReviews() > 0;
    }

    public boolean hasRecoveryProgress() {
        return state != null && state.wrongReviewDoneCount() > 0;
    }

    public boolean isRecoverySafe() {
        return state != null && state.wrongReviewDoneCount() > state.wrongReviews();
    }
}