package com.goosage.app.recovery.message.state;

import com.goosage.app.recovery.message.domain.DomainResolutionResult;
import com.goosage.app.recovery.message.domain.DomainResolverShadow;
import com.goosage.app.recovery.message.event.EventDescriptor;
import com.goosage.app.recovery.message.event.EventDescriptorResolverShadow;
import com.goosage.app.recovery.message.signalshadow.EventSignalMapperShadow;
import com.goosage.app.recovery.message.signalshadow.ShadowSignalVector;
import com.goosage.app.recovery.message.urgeshadow.UrgeResolverShadow;

public class CurrentStateResolverShadow {

    private final DomainResolverShadow domainResolver =
            new DomainResolverShadow();

    private final EventDescriptorResolverShadow eventResolver =
            new EventDescriptorResolverShadow();

    private final EventSignalMapperShadow signalMapper =
            new EventSignalMapperShadow();

    private final UrgeResolverShadow urgeResolver =
            new UrgeResolverShadow();

    public CurrentStateShadow resolve(String message) {
        DomainResolutionResult domain =
                domainResolver.resolve(message);

        EventDescriptor event =
                eventResolver.resolve(message);

        ShadowSignalVector eventSignals =
                signalMapper.map(event);

        int urge =
                urgeResolver.resolve(message).urge();

        ShadowSignalVector signals =
                signalMapper.mergeUrge(
                        eventSignals,
                        urge
                );

        return new CurrentStateShadow(
                domain.domain(),
                event.actionType(),
                event.actionStage(),
                event.completed(),
                event.stopCause(),
                signals
        );
    }
}
