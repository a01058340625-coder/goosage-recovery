package com.goosage.app.recovery.message;

import com.goosage.app.recovery.message.brain.BrainRecoveryDryRunResult;
import com.goosage.domain.recovery.RecoverySnapshot;

public record RecoveryMessageProjection(
        RecoveryMessageAnalysis analysis,
        RecoverySnapshot baseSnapshot,
        RecoverySnapshot projectedSnapshot,
        BrainRecoveryDryRunResult brainResult
) {

    public RecoveryMessageProjection(
            RecoveryMessageAnalysis analysis,
            RecoverySnapshot baseSnapshot,
            RecoverySnapshot projectedSnapshot
    ) {
        this(
                analysis,
                baseSnapshot,
                projectedSnapshot,
                BrainRecoveryDryRunResult.notRequested()
        );
    }

    public boolean projected() {
        return projectedSnapshot != null;
    }
}