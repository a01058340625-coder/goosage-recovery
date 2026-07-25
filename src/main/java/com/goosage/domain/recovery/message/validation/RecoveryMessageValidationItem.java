package com.goosage.domain.recovery.message.validation;

import java.time.LocalDateTime;

public record RecoveryMessageValidationItem(
        long id,
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
        boolean virtualUserCandidate,

        LocalDateTime createdAt
) {
}