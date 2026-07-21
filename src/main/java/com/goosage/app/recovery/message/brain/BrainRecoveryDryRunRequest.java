package com.goosage.app.recovery.message.brain;

import com.goosage.domain.recovery.RecoverySnapshot;
import com.goosage.domain.recovery.RecoveryState;

public record BrainRecoveryDryRunRequest(
        Long userId,
        int recentEventCount3d,
        int streakDays,
        int daysSinceLastEvent,
        int urgeLogs,
        int betAttempts,
        int betBlockedCount,
        int recoveryActionCount,
        int relapseSignalCount
) {

    public static BrainRecoveryDryRunRequest from(
            long userId,
            RecoverySnapshot snapshot
    ) {
        if (userId <= 0) {
            throw new IllegalArgumentException(
                    "valid userId is required"
            );
        }

        if (snapshot == null) {
            throw new IllegalArgumentException(
                    "snapshot is required"
            );
        }

        RecoveryState state = snapshot.state();

        return new BrainRecoveryDryRunRequest(
                userId,
                snapshot.recentEventCount3d(),
                snapshot.streakDays(),
                snapshot.daysSinceLastEvent(),
                state == null ? 0 : state.urgeLogs(),
                state == null ? 0 : state.betAttempts(),
                state == null ? 0 : state.betBlockedCount(),
                state == null ? 0 : state.recoveryActionCount(),
                state == null ? 0 : state.relapseSignalCount()
        );
    }
}