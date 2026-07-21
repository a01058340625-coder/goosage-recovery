package com.goosage.api.view.recovery.message;

import com.goosage.app.recovery.message.RecoveryMessageProjection;
import com.goosage.domain.recovery.RecoverySnapshot;
import com.goosage.domain.recovery.RecoveryState;
import com.goosage.domain.recovery.message.RecoveryMessageSignal;

public record RecoveryMessageAnalyzeResponse(
        boolean analyzable,
        String holdReason,
        String originalMessage,
        SignalResponse signal,
        SnapshotResponse baseSnapshot,
        SnapshotResponse projectedSnapshot
) {

    public static RecoveryMessageAnalyzeResponse from(
            RecoveryMessageProjection projection
    ) {
        if (projection == null) {
            throw new IllegalArgumentException("projection is required");
        }

        RecoveryMessageSignal signal =
                projection.analysis() == null
                        ? null
                        : projection.analysis().signal();

        return new RecoveryMessageAnalyzeResponse(
                projection.analysis() != null
                        && projection.analysis().analyzable(),
                projection.analysis() == null
                        ? "ANALYSIS_MISSING"
                        : projection.analysis().holdReason(),
                projection.analysis() == null
                        ? null
                        : projection.analysis().originalMessage(),
                SignalResponse.from(signal),
                SnapshotResponse.from(projection.baseSnapshot()),
                SnapshotResponse.from(projection.projectedSnapshot())
        );
    }

    public record SignalResponse(
            int urgeLogDelta,
            int betAttemptDelta,
            int betBlockedDelta,
            int recoveryActionDelta,
            int relapseSignalDelta,
            double confidence,
            String reason
    ) {

        public static SignalResponse from(RecoveryMessageSignal signal) {
            if (signal == null) {
                return null;
            }

            return new SignalResponse(
                    signal.urgeLogDelta(),
                    signal.betAttemptDelta(),
                    signal.betBlockedDelta(),
                    signal.recoveryActionDelta(),
                    signal.relapseSignalDelta(),
                    signal.confidence(),
                    signal.reason()
            );
        }
    }

    public record SnapshotResponse(
            int urgeLogs,
            int betAttempts,
            int betBlockedCount,
            int recoveryActionCount,
            int relapseSignalCount,
            int eventsCount,
            int streakDays,
            int daysSinceLastEvent,
            int recentEventCount3d
    ) {

        public static SnapshotResponse from(RecoverySnapshot snapshot) {
            if (snapshot == null) {
                return null;
            }

            RecoveryState state = snapshot.state();

            return new SnapshotResponse(
                    state == null ? 0 : state.urgeLogs(),
                    state == null ? 0 : state.betAttempts(),
                    state == null ? 0 : state.betBlockedCount(),
                    state == null ? 0 : state.recoveryActionCount(),
                    state == null ? 0 : state.relapseSignalCount(),
                    state == null ? 0 : state.eventsCount(),
                    snapshot.streakDays(),
                    snapshot.daysSinceLastEvent(),
                    snapshot.recentEventCount3d()
            );
        }
    }
}