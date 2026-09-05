package com.goosage.app.recovery.message.signalshadow;

import com.goosage.app.recovery.message.action.ActionStage;
import com.goosage.app.recovery.message.action.ActionType;
import com.goosage.app.recovery.message.event.EventDescriptor;
import com.goosage.app.recovery.message.stopcause.StopCauseType;

public class EventSignalMapperShadow {

    public ShadowSignalVector map(
            EventDescriptor event
    ) {
        if (event == null) {
            return zero();
        }

        int urge = 0;
        int attempt = 0;
        int blocked = 0;
        int recovery = 0;
        int relapse = 0;

        ActionType type = event.actionType();
        ActionStage stage = event.actionStage();
        StopCauseType stopCause = event.stopCause();

        if (
                type == ActionType.SEARCH
                || type == ActionType.ACCESS
                || type == ActionType.LOGIN
                || type == ActionType.FUNDING
                || type == ActionType.WAGER
                || type == ActionType.ACCOUNT_CONTROL
        ) {
            if (
                    stage == ActionStage.INPUT
                    || stage == ActionStage.SUBMITTED
                    || stage == ActionStage.COMPLETED
            ) {
                attempt = 1;
            }
        }

        if (stopCause == StopCauseType.SELF_STOP) {
            blocked = 1;
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
    public ShadowSignalVector mapAll(
            java.util.List<com.goosage.app.recovery.message.event.EventDescriptor> events
    ) {
        if (events == null || events.isEmpty()) {
            return zero();
        }

        int urge = 0;
        int attempt = 0;
        int blocked = 0;
        int recovery = 0;
        int relapse = 0;

        for (com.goosage.app.recovery.message.event.EventDescriptor event : events) {
            ShadowSignalVector current = map(event);

            urge = Math.max(urge, current.urge());
            attempt = Math.max(attempt, current.attempt());
            blocked = Math.max(blocked, current.blocked());
            recovery = Math.max(recovery, current.recovery());
            relapse = Math.max(relapse, current.relapse());
        }

        return new ShadowSignalVector(
                urge,
                attempt,
                blocked,
                recovery,
                relapse
        );
    }

    public ShadowSignalVector mergeUrge(
            ShadowSignalVector eventSignals,
            int urge
    ) {
        if (eventSignals == null) {
            eventSignals = zero();
        }

        return new ShadowSignalVector(
                Math.max(eventSignals.urge(), urge),
                eventSignals.attempt(),
                eventSignals.blocked(),
                eventSignals.recovery(),
                eventSignals.relapse()
        );
    }

    private ShadowSignalVector zero() {
        return new ShadowSignalVector(
                0, 0, 0, 0, 0
        );
    }
}
