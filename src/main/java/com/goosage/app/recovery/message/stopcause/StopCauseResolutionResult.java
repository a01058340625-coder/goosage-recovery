package com.goosage.app.recovery.message.stopcause;

import java.util.List;

public record StopCauseResolutionResult(
        StopCauseType stopCause,
        double confidence,
        List<String> evidence
) {
}
