package com.goosage.app.recovery.message.stopcause;

public record StopCauseEventInput(
        String eventText,
        String actionText,
        String outcomeText
) {
}
