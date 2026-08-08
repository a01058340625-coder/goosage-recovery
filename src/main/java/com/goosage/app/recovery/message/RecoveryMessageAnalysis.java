package com.goosage.app.recovery.message;

import com.goosage.domain.recovery.message.RecoveryMessageSignal;
import com.goosage.domain.recovery.message.RecoveryPostBlockStateMetadata;
import com.goosage.domain.recovery.message.RecoveryReentryPreparationMetadata;
import com.goosage.domain.recovery.message.RecoveryRiskPreparationMetadata;

public record RecoveryMessageAnalysis(
        String originalMessage,
        boolean analyzable,
        RecoveryMessageSignal signal,
        String holdReason,
        RecoveryRiskPreparationMetadata riskPreparationMetadata,
        RecoveryPostBlockStateMetadata postBlockStateMetadata,
        RecoveryReentryPreparationMetadata reentryPreparationMetadata
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
                RecoveryPostBlockStateMetadata.none(),
                RecoveryReentryPreparationMetadata.none()
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
                RecoveryPostBlockStateMetadata.none(),
                RecoveryReentryPreparationMetadata.none()
        );
    }

    public RecoveryMessageAnalysis(
            String originalMessage,
            boolean analyzable,
            RecoveryMessageSignal signal,
            String holdReason,
            RecoveryRiskPreparationMetadata riskPreparationMetadata,
            RecoveryPostBlockStateMetadata postBlockStateMetadata
    ) {
        this(
                originalMessage,
                analyzable,
                signal,
                holdReason,
                riskPreparationMetadata,
                postBlockStateMetadata,
                RecoveryReentryPreparationMetadata.none()
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

        if (reentryPreparationMetadata == null) {
            reentryPreparationMetadata =
                    RecoveryReentryPreparationMetadata.none();
        }
    }
}