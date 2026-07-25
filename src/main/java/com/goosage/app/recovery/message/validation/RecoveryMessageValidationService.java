package com.goosage.app.recovery.message.validation;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationCommand;
import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationItem;
import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationQuery;
import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationPort;
import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationResult;

@Service
public class RecoveryMessageValidationService {

    private static final Set<String> VALIDATION_RESULTS =
            Set.of(
                    "MATCH",
                    "MISMATCH",
                    "NEEDS_REVIEW"
            );

    private static final Set<String> MISMATCH_TYPES =
            Set.of(
                    "SIGNAL",
                    "HOLD_REASON",
                    "PATTERN",
                    "NEXT_ACTION",
                    "RECOMMENDATION",
                    "MULTIPLE",
                    "OTHER"
            );

    private final RecoveryMessageValidationPort validationPort;

    public RecoveryMessageValidationService(
            RecoveryMessageValidationPort validationPort
    ) {
        this.validationPort = validationPort;
    }

    public RecoveryMessageValidationResult save(
            RecoveryMessageValidationCommand command
    ) {
        if (command == null) {
            throw new IllegalArgumentException(
                    "validation command is required"
            );
        }

        String validationResult =
                normalizeRequired(
                        command.validationResult(),
                        "validationResult"
                );

        if (!VALIDATION_RESULTS.contains(validationResult)) {
            throw new IllegalArgumentException(
                    "unsupported validationResult"
            );
        }

        String mismatchType =
                normalizeOptionalUppercase(
                        command.mismatchType()
                );

        if (
                "MATCH".equals(validationResult)
                && mismatchType != null
        ) {
            throw new IllegalArgumentException(
                    "MATCH must not have mismatchType"
            );
        }

        if (
                "MISMATCH".equals(validationResult)
                && mismatchType == null
        ) {
            throw new IllegalArgumentException(
                    "MISMATCH requires mismatchType"
            );
        }

        if (
                mismatchType != null
                && !MISMATCH_TYPES.contains(mismatchType)
        ) {
            throw new IllegalArgumentException(
                    "unsupported mismatchType"
            );
        }

        RecoveryMessageValidationCommand normalized =
                new RecoveryMessageValidationCommand(
                        command.userId(),
                        command.originalMessage().trim(),

                        command.expectedAnalyzable(),
                        normalizeOptional(
                                command.expectedHoldReason()
                        ),
                        normalizeOptional(
                                command.expectedSignalJson()
                        ),
                        normalizeOptional(
                                command.expectedPatternType()
                        ),
                        normalizeOptional(
                                command.expectedNextActionType()
                        ),

                        command.actualAnalyzable(),
                        normalizeOptional(
                                command.actualHoldReason()
                        ),
                        normalizeOptional(
                                command.actualSignalJson()
                        ),
                        normalizeOptional(
                                command.actualPatternType()
                        ),
                        normalizeOptional(
                                command.actualNextActionType()
                        ),
                        normalizeOptional(
                                command.actualRecommendedAction()
                        ),

                        validationResult,
                        mismatchType,
                        normalizeOptional(
                                command.reviewMemo()
                        ),

                        command.realScenarioCandidate(),
                        command.virtualUserCandidate()
                );

        return validationPort.save(normalized);
    }

    public List<RecoveryMessageValidationItem> findRecent(
            RecoveryMessageValidationQuery query
    ) {
        if (query == null) {
            throw new IllegalArgumentException(
                    "validation query is required"
            );
        }

        String validationResult =
                normalizeOptionalUppercase(
                        query.validationResult()
                );

        if (
                validationResult != null
                && !VALIDATION_RESULTS.contains(
                        validationResult
                )
        ) {
            throw new IllegalArgumentException(
                    "unsupported validationResult"
            );
        }

        if (
                query.limit() < 1
                || query.limit() > 100
        ) {
            throw new IllegalArgumentException(
                    "limit must be between 1 and 100"
            );
        }

        RecoveryMessageValidationQuery normalized =
                new RecoveryMessageValidationQuery(
                        query.userId(),
                        validationResult,
                        query.realScenarioCandidate(),
                        query.virtualUserCandidate(),
                        query.limit()
                );

        return validationPort.findRecent(normalized);
    }

    private String normalizeRequired(
            String value,
            String fieldName
    ) {
        if (
                value == null
                || value.isBlank()
        ) {
            throw new IllegalArgumentException(
                    fieldName + " is required"
            );
        }

        return value.trim().toUpperCase();
    }

    private String normalizeOptionalUppercase(
            String value
    ) {
        String normalized =
                normalizeOptional(value);

        return normalized == null
                ? null
                : normalized.toUpperCase();
    }

    private String normalizeOptional(String value) {
        if (
                value == null
                || value.isBlank()
        ) {
            return null;
        }

        return value.trim();
    }
}