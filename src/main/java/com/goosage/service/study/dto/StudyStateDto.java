package com.goosage.service.study.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record StudyStateDto
(
        java.time.LocalDate ymd,
        boolean studiedToday,
        int streakDays,
        int eventsCount,
        int quizSubmits,
        int wrongReviews,
        java.time.LocalDateTime lastEventAt,
        Long recentKnowledgeId
) {}

