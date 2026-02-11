package com.goosage.api.view.study;

public record StudyEventRequest(
        String type,     // 예: "COACH_OPEN", "WRONG_REVIEW_DONE"
        Long knowledgeId // 없으면 null
) {}
