package com.goosage.app.recovery.message.brain;

public record BrainRecoveryDryRunResponse(
        String patternType,
        double score,
        String reason,
        double topScore,
        double secondScore,
        double gap,
        String gapClass,
        String nextActionType,
        String actionGuide,
        String actionIntensity,
        String actionTarget,
        String domainActionType,
        String domainActionGuide,
        String recommendedAction,
        double recommendationConfidence,
        String recommendationSource
) {
}