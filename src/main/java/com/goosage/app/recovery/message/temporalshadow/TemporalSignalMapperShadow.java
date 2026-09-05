package com.goosage.app.recovery.message.temporalshadow;

import com.goosage.app.recovery.message.action.ActionStage;
import com.goosage.app.recovery.message.action.ActionType;
import com.goosage.app.recovery.message.event.EventDescriptor;
import com.goosage.app.recovery.message.stopcause.StopCauseType;
import com.goosage.app.recovery.message.signalshadow.ShadowSignalVector;

public class TemporalSignalMapperShadow {

    public ShadowSignalVector map(
            TemporalEventDescriptorShadow temporalEvent
    ) {
        TemporalRoleShadow role =
                temporalEvent.temporalRole();

        EventDescriptor event =
                temporalEvent.event();

        int urge = 0;
        int attempt = 0;
        int blocked = 0;
        int recovery = 0;
        int relapse = 0;

        if (
                role == TemporalRoleShadow.PAST
                && event.actionType() == ActionType.WAGER
                && event.actionStage() == ActionStage.COMPLETED
        ) {
            relapse = 1;
        }

        if (event.stopCause() == StopCauseType.SELF_STOP) {
            blocked = 1;
        }
        if (
                role == TemporalRoleShadow.CURRENT
                || role == TemporalRoleShadow.RECENT_PAST
        ) {
            if (
                    event.actionType() != ActionType.RECOVERY
                    && event.actionStage() != ActionStage.THOUGHT
                    && event.actionStage() != ActionStage.UNKNOWN
            ) {
                attempt = 1;
            }



            if (
                    event.actionType() == ActionType.RECOVERY
                    && event.actionStage() == ActionStage.COMPLETED
            ) {
                recovery = 1;
            }

            if (
                    event.actionType() == ActionType.WAGER
                    && event.actionStage() == ActionStage.COMPLETED
            ) {
                relapse = 1;
            }
        }

        return new ShadowSignalVector(
                urge,
                attempt,
                blocked,
                recovery,
                relapse
        );
    }
}