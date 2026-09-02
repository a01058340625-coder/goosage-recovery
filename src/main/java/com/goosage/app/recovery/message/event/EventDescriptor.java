package com.goosage.app.recovery.message.event;

import com.goosage.app.recovery.message.action.ActionStage;
import com.goosage.app.recovery.message.action.ActionType;
import com.goosage.app.recovery.message.stopcause.StopCauseType;

public record EventDescriptor(
        EventType eventType,
        ActionType actionType,
        ActionStage actionStage,
        boolean completed,
        StopCauseType stopCause
) {
}
