package com.goosage.domain.recovery.message;

import java.util.Set;

public record RecoveryRiskPreparationMetadata(
        boolean detected,
        String type,
        double confidence,
        String reason
) {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "FUNDING_COMPLETED_BET_NEGATED",
            "FUNDING_COMPLETED_FUTURE_INTENT_PRESENT",
            "FUNDING_STARTED_THEN_CANCELLED",
            "FUNDING_INTERRUPTED_BY_EXTERNAL_INTERVENTION_WITH_RETRY_INTENT"
    );

    public RecoveryRiskPreparationMetadata {
        if (!detected) {
            if (type != null) {
                throw new IllegalArgumentException(
                        "type must be null when not detected"
                );
            }

            if (confidence != 0.0) {
                throw new IllegalArgumentException(
                        "confidence must be 0 when not detected"
                );
            }

            if (reason != null) {
                throw new IllegalArgumentException(
                        "reason must be null when not detected"
                );
            }
        } else {
            if (
                    type == null
                    || !ALLOWED_TYPES.contains(type)
            ) {
                throw new IllegalArgumentException(
                        "unsupported risk preparation type"
                );
            }

            if (
                    confidence <= 0.0
                    || confidence > 1.0
            ) {
                throw new IllegalArgumentException(
                        "confidence must be greater than 0 and at most 1"
                );
            }

            if (
                    reason == null
                    || reason.isBlank()
            ) {
                throw new IllegalArgumentException(
                        "reason is required when detected"
                );
            }
        }
    }

    public static RecoveryRiskPreparationMetadata none() {
        return new RecoveryRiskPreparationMetadata(
                false,
                null,
                0.0,
                null
        );
    }

    public static RecoveryRiskPreparationMetadata detected(
            String type,
            double confidence,
            String reason
    ) {
        return new RecoveryRiskPreparationMetadata(
                true,
                type,
                confidence,
                reason
        );
    }
}
