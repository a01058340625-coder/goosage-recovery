package com.goosage.app.recovery.message.structuredshadow;

import com.goosage.app.recovery.message.event.EventDescriptor;
import com.goosage.app.recovery.message.temporalshadow.TemporalRoleShadow;

public record StructuredEventShadow(
        String text,
        TemporalRoleShadow temporalRole,
        EventDescriptor event,
        boolean protectiveOutcome
) {
}