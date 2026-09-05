package com.goosage.app.recovery.message.temporalshadow;

import com.goosage.app.recovery.message.event.EventDescriptor;

public record TemporalEventDescriptorShadow(
        TemporalRoleShadow temporalRole,
        EventDescriptor event
) {
}