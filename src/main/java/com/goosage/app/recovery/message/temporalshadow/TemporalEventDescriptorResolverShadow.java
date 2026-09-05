package com.goosage.app.recovery.message.temporalshadow;

import com.goosage.app.recovery.message.event.EventDescriptor;
import com.goosage.app.recovery.message.event.EventDescriptorResolverShadow;

public class TemporalEventDescriptorResolverShadow {

    private final TemporalContextResolverShadow temporalResolver =
            new TemporalContextResolverShadow();

    private final EventDescriptorResolverShadow eventResolver =
            new EventDescriptorResolverShadow();

    public TemporalEventDescriptorShadow resolve(
            String eventText,
            TemporalRoleShadow previousRole
    ) {
        TemporalRoleShadow role =
                temporalResolver.resolve(eventText, previousRole);

        EventDescriptor event =
                eventResolver.resolve(eventText);

        return new TemporalEventDescriptorShadow(
                role,
                event
        );
    }
}