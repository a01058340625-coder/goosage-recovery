package com.goosage.app.recovery.message;

import com.goosage.domain.recovery.message.RecoveryMessageSignal;
import com.goosage.domain.recovery.message.RecoveryRiskPreparationMetadata;

public record RecoveryMessageAnalysis(
        String originalMessage,
        boolean analyzable,
        RecoveryMessageSignal signal,
        String holdReason,
        RecoveryRiskPreparationMetadata riskPreparationMetadata
) {

    public RecoveryMessageAnalysis(
            String originalMessage,
            boolean analyzable,
            RecoveryMessageSignal signal,
            String holdReason
    ) {
        this(
                originalMessage,
                analyzable,
                signal,
                holdReason,
                RecoveryRiskPreparationMetadata.none()
        );
    }

    public RecoveryMessageAnalysis {
        if (riskPreparationMetadata == null) {
            riskPreparationMetadata =
                    RecoveryRiskPreparationMetadata.none();
        }
    }
}