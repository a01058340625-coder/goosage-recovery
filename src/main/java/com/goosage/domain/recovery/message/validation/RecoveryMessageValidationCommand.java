package com.goosage.domain.recovery.message.validation;

public record RecoveryMessageValidationCommand(
        long userId,
        String originalMessage,

        Boolean expectedAnalyzable,
        String expectedHoldReason,
        String expectedSignalJson,
        String expectedPatternType,
        String expectedNextActionType,

        boolean actualAnalyzable,
        String actualHoldReason,
        String actualSignalJson,
        String actualPatternType,
        String actualNextActionType,
        String actualRecommendedAction,

        String validationResult,
        String mismatchType,
        String reviewMemo,

        boolean realScenarioCandidate,
        boolean virtualUserCandidate
) {

    public RecoveryMessageValidationCommand {
        if (userId <= 0) {
            throw new IllegalArgumentException(
                    "valid userId is required"
            );
        }

        if (
                originalMessage == null
                || originalMessage.isBlank()
        ) {
            throw new IllegalArgumentException(
                    "originalMessage is required"
            );
        }

        if (
                validationResult == null
                || validationResult.isBlank()
        ) {
            throw new IllegalArgumentException(
                    "validationResult is required"
            );
        }
    }
}