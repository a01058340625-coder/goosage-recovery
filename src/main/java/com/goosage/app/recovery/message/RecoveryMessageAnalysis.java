package com.goosage.app.recovery.message;

import com.goosage.domain.recovery.message.RecoveryMessageSignal;
import com.goosage.domain.recovery.message.RecoveryPostBlockStateMetadata;
import com.goosage.domain.recovery.message.RecoveryRiskPreparationMetadata;

public record RecoveryMessageAnalysis(
        String originalMessage,
        boolean analyzable,
        RecoveryMessageSignal signal,
        String holdReason,
        RecoveryRiskPreparationMetadata riskPreparationMetadata,
        RecoveryPostBlockStateMetadata postBlockStateMetadata
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
                RecoveryRiskPreparationMetadata.none(),
                RecoveryPostBlockStateMetadata.none()
        );
    }

    public RecoveryMessageAnalysis(
            String originalMessage,
            boolean analyzable,
            RecoveryMessageSignal signal,
            String holdReason,
            RecoveryRiskPreparationMetadata riskPreparationMetadata
    ) {
        this(
                originalMessage,
                analyzable,
                signal,
                holdReason,
                riskPreparationMetadata,
                RecoveryPostBlockStateMetadata.none()
        );
    }

    public RecoveryMessageAnalysis {
        if (riskPreparationMetadata == null) {
            riskPreparationMetadata =
                    RecoveryRiskPreparationMetadata.none();
        }

        if (postBlockStateMetadata == null) {
            postBlockStateMetadata =
                    RecoveryPostBlockStateMetadata.none();
        }
    }
}