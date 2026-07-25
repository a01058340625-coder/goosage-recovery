package com.goosage.domain.recovery.message.validation;

import java.time.LocalDateTime;

public record RecoveryMessageValidationResult(
        long id,
        long userId,
        String validationResult,
        boolean realScenarioCandidate,
        boolean virtualUserCandidate,
        LocalDateTime createdAt
) {
}