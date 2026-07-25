package com.goosage.api.view.recovery.message.validation;

import java.time.LocalDateTime;

import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationResult;

public record RecoveryMessageValidationSaveResponse(
        long id,
        long userId,
        String validationResult,
        boolean realScenarioCandidate,
        boolean virtualUserCandidate,
        LocalDateTime createdAt
) {

    public static RecoveryMessageValidationSaveResponse from(
            RecoveryMessageValidationResult result
    ) {
        if (result == null) {
            throw new IllegalArgumentException(
                    "validation result is required"
            );
        }

        return new RecoveryMessageValidationSaveResponse(
                result.id(),
                result.userId(),
                result.validationResult(),
                result.realScenarioCandidate(),
                result.virtualUserCandidate(),
                result.createdAt()
        );
    }
}