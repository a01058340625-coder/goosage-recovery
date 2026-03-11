package com.goosage.api.view.study;

import com.goosage.domain.EventType;

public record StudyEventRequest(
        EventType type,
        Long knowledgeId
) {}