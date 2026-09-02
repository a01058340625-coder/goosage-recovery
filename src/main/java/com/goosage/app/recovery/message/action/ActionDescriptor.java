package com.goosage.app.recovery.message.action;

import java.util.List;

public record ActionDescriptor(
        ActionType actionType,
        ActionStage actionStage,
        boolean completed,
        double confidence,
        List<String> evidence
) {
}
