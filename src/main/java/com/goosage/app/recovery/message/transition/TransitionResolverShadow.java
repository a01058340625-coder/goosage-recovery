package com.goosage.app.recovery.message.transition;

import com.goosage.app.recovery.message.event.EventDescriptor;
import com.goosage.app.recovery.message.event.EventDescriptorResolverShadow;
import com.goosage.app.recovery.message.state.CurrentStateResolverShadow;
import com.goosage.app.recovery.message.state.CurrentStateShadow;

public class TransitionResolverShadow {

    private final CurrentStateResolverShadow stateResolver =
            new CurrentStateResolverShadow();

    private final EventDescriptorResolverShadow eventResolver =
            new EventDescriptorResolverShadow();

    public TransitionShadow resolve(
            String beforeText,
            String eventText,
            String afterText
    ) {
        CurrentStateShadow beforeState =
                stateResolver.resolve(beforeText);

        EventDescriptor rawEvent =
                eventResolver.resolve(eventText);

        EventDescriptor event =
                rawEvent.actionType()
                        == com.goosage.app.recovery.message.action.ActionType.UNKNOWN
                        && beforeState.actionType()
                        != com.goosage.app.recovery.message.action.ActionType.UNKNOWN
                ? new EventDescriptor(
                        rawEvent.eventType(),
                        beforeState.actionType(),
                        rawEvent.actionStage(),
                        rawEvent.completed(),
                        rawEvent.stopCause()
                )
                : rawEvent;

        CurrentStateShadow rawAfterState =
                stateResolver.resolve(afterText);

        var eventSignal =
                new com.goosage.app.recovery.message.signalshadow.EventSignalMapperShadow()
                        .map(event);

        var beforeSignal =
                beforeState.signalVector();

        var afterSignal =
                new com.goosage.app.recovery.message.signalshadow.ShadowSignalVector(
                        Math.max(beforeSignal.urge(), eventSignal.urge()),
                        Math.max(beforeSignal.attempt(), eventSignal.attempt()),
                        Math.max(beforeSignal.blocked(), eventSignal.blocked()),
                        Math.max(beforeSignal.recovery(), eventSignal.recovery()),
                        Math.max(beforeSignal.relapse(), eventSignal.relapse())
                );

        CurrentStateShadow afterState =
                new CurrentStateShadow(
                        rawAfterState.domain(),
                        event.actionType(),
                        event.actionStage(),
                        event.completed(),
                        event.stopCause(),
                        afterSignal
                );

        return new TransitionShadow(
                beforeState,
                event,
                afterState
        );
    }
}
