package com.goosage.domain.recovery.message;

import java.util.Set;

public record RecoveryReentryStateMetadata(
        boolean detected,
        String type,
        double confidence,
        String reason
) {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "REENTRY_LOGIN_COMPLETED",
            "REENTRY_FUNDING_COMPLETED",
            "POST_BLOCK_REENTRY_LOGIN_COMPLETED",
        "POST_BLOCK_REENTRY_FUNDING_COMPLETED",
        "POST_BLOCK_WAGER_ATTEMPT_FAILED"
    );

    public RecoveryReentryStateMetadata {
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
                        "unsupported reentry preparation type"
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

    public static RecoveryReentryStateMetadata none() {
        return new RecoveryReentryStateMetadata(
                false,
                null,
                0.0,
                null
        );
    }

    public static RecoveryReentryStateMetadata detected(
            String type,
            double confidence,
            String reason
    ) {
        return new RecoveryReentryStateMetadata(
                true,
                type,
                confidence,
                reason
        );
    }
}
