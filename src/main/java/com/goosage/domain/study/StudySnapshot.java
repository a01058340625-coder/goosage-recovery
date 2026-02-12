package com.goosage.domain.study;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record StudySnapshot(
        LocalDate ymd,
        StudyState state,
        boolean studiedToday,
        int streakDays,
        LocalDateTime lastEventAt,
        int daysSinceLastEvent,
        int recentEventCount3d,
        Long recentKnowledgeId
) {}
