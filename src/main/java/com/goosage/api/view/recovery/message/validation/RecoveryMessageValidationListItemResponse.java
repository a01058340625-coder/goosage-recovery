package com.goosage.api.view.recovery.message.validation;

import java.time.LocalDateTime;

public record RecoveryMessageValidationListItemResponse(
        long id,
        long userId,
        String originalMessage,

        Boolean expectedAnalyzable,
        String expectedHoldReason,
        Object expectedSignal,
        String expectedPatternType,
        String expectedNextActionType,

        boolean actualAnalyzable,
        String actualHoldReason,
        Object actualSignal,
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