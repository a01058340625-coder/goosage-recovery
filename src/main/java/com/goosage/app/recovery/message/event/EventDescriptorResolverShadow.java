package com.goosage.app.recovery.message.event;

import com.goosage.app.recovery.message.action.ActionDescriptor;
import com.goosage.app.recovery.message.action.ActionDescriptorResolverShadow;
import com.goosage.app.recovery.message.action.ActionType;
import com.goosage.app.recovery.message.stopcause.StopCauseResolutionResult;
import com.goosage.app.recovery.message.stopcause.StopCauseResolverShadow;

public class EventDescriptorResolverShadow {

    private final ActionDescriptorResolverShadow actionResolver =
            new ActionDescriptorResolverShadow();

    private final StopCauseResolverShadow stopCauseResolver =
            new StopCauseResolverShadow();

    public EventDescriptor resolve(String eventText) {
        ActionDescriptor action =
                actionResolver.resolve(eventText);

        StopCauseResolutionResult stop =
                stopCauseResolver.resolve(eventText);

        return new EventDescriptor(
                mapEventType(action.actionType()),
                action.actionType(),
                action.actionStage(),
                action.completed(),
                stop.stopCause()
        );
    }

    private EventType mapEventType(
            ActionType actionType
    ) {
        return switch (actionType) {
            case SEARCH -> EventType.SEARCH_INPUT;
            case ACCESS -> EventType.SITE_ACCESS;
            case LOGIN -> EventType.LOGIN;
            case FUNDING -> EventType.FUNDING;
            case WAGER -> EventType.WAGER;
            case RECOVERY -> EventType.RECOVERY_ACTION;
            case ACCOUNT_CONTROL -> EventType.ACCOUNT_CONTROL;
            case UNKNOWN -> EventType.UNKNOWN;
        };
    }
}
