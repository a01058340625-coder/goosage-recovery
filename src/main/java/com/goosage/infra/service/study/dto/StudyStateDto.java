package com.goosage.infra.service.study.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.goosage.domain.study.StudyState;

public record StudyStateDto(
    LocalDate ymd,
    boolean studiedToday,
    int streakDays,
    int eventsCount,
    int quizSubmits,
    int wrongReviews,
    LocalDateTime lastEventAt,
    Long recentKnowledgeId
) {
    // ✅ 과거 코드 호환용 getter (Mapper에서 getXxx() 쓰는 경우 바로 살림)
    public LocalDate getYmd() { return ymd; }
    public boolean isStudiedToday() { return studiedToday; }
    public int getStreakDays() { return streakDays; }
    public int getEventsCount() { return eventsCount; }
    public int getQuizSubmits() { return quizSubmits; }
    public int getWrongReviews() { return wrongReviews; }
    public LocalDateTime getLastEventAt() { return lastEventAt; }
    public Long getRecentKnowledgeId() { return recentKnowledgeId; }

    // ✅ 엔진 state만으로도 DTO를 만들 수 있게 "최소 생성자" 제공 (임시 기본값)
    public StudyStateDto(StudyState s) {
        this(
            LocalDate.now(),
            s.eventsCount() > 0,
            0,
            s.eventsCount(),
            s.quizSubmits(),
            s.wrongReviews(),
            null,
            null
        );
    }
}
