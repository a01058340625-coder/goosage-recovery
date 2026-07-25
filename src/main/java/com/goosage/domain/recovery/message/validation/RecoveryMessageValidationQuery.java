package com.goosage.domain.recovery.message.validation;

public record RecoveryMessageValidationQuery(
        long userId,
        String validationResult,
        Boolean realScenarioCandidate,
        Boolean virtualUserCandidate,
        int limit
) {
}