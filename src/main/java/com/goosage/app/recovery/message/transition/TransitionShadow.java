package com.goosage.app.recovery.message.transition;

import com.goosage.app.recovery.message.event.EventDescriptor;
import com.goosage.app.recovery.message.state.CurrentStateShadow;

public record TransitionShadow(
        CurrentStateShadow beforeState,
        EventDescriptor event,
        CurrentStateShadow afterState
) {
}
