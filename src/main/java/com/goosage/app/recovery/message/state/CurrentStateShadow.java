package com.goosage.app.recovery.message.state;

import com.goosage.app.recovery.message.action.ActionStage;
import com.goosage.app.recovery.message.action.ActionType;
import com.goosage.app.recovery.message.domain.DomainType;
import com.goosage.app.recovery.message.signalshadow.ShadowSignalVector;
import com.goosage.app.recovery.message.stopcause.StopCauseType;

public record CurrentStateShadow(
        DomainType domain,
        ActionType actionType,
        ActionStage actionStage,
        boolean completed,
        StopCauseType stopCause,
        ShadowSignalVector signalVector
) {
}
