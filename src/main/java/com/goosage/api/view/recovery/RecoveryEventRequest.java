package com.goosage.api.view.recovery;

import com.goosage.domain.EventType;

public record RecoveryEventRequest(
        EventType type,
        Long knowledgeId
) {
}