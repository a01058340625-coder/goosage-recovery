package com.goosage.app.recovery.message.structuredshadow;

import com.goosage.app.recovery.message.action.ActionStage;
import com.goosage.app.recovery.message.action.ActionType;
import com.goosage.app.recovery.message.signalshadow.ShadowSignalVector;
import com.goosage.app.recovery.message.temporalshadow.TemporalRoleShadow;
import com.goosage.app.recovery.message.urgeshadow.UrgeResolverShadow;

public class StructuredSignalMapperShadow {

    private final UrgeResolverShadow urgeResolver =
            new UrgeResolverShadow();

    public ShadowSignalVector map(
            StructuredEventShadow structured
    ) {
        ActionType type =
                structured.event().actionType();

        ActionStage stage =
                structured.event().actionStage();

        TemporalRoleShadow role =
                structured.temporalRole();

        int urge =
                urgeResolver.resolve(
                        structured.text()
                ).urge();

        int attempt = 0;
        int blocked =
                structured.protectiveOutcome()
                        ? 1
                        : 0;

        int recovery = 0;
        int relapse = 0;

        if (
                type != ActionType.ACCOUNT_CONTROL
                && type != ActionType.RECOVERY
                && stage != ActionStage.THOUGHT
                && stage != ActionStage.UNKNOWN
        ) {
            attempt = 1;
        }

        if (
                role == TemporalRoleShadow.PAST
                && type == ActionType.WAGER
                && stage == ActionStage.COMPLETED
        ) {
            attempt = 0;
        }

        if (
                type == ActionType.RECOVERY
                && stage == ActionStage.COMPLETED
        ) {
            recovery = 1;
        }

        if (
                type == ActionType.WAGER
                && stage == ActionStage.COMPLETED
        ) {
            relapse = 1;
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