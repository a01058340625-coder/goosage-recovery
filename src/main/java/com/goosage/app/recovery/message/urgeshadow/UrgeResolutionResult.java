package com.goosage.app.recovery.message.urgeshadow;

import java.util.List;

public record UrgeResolutionResult(
        int urge,
        double confidence,
        List<String> evidence
) {
}
