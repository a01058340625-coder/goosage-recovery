package com.goosage.app.recovery.message;

import com.goosage.domain.recovery.RecoverySnapshot;

public record RecoveryMessageProjection(
        RecoveryMessageAnalysis analysis,
        RecoverySnapshot baseSnapshot,
        RecoverySnapshot projectedSnapshot
) {

    public boolean projected() {
        return projectedSnapshot != null;
    }
}