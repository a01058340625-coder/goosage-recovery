package com.goosage.app.recovery.message;

import org.springframework.stereotype.Component;

import com.goosage.domain.recovery.message.RecoveryMessageSignal;
import com.goosage.domain.recovery.RecoverySnapshot;
import com.goosage.domain.recovery.RecoveryState;

@Component
public class RecoverySnapshotProjector {

    public RecoverySnapshot project(
            RecoverySnapshot base,
            RecoveryMessageSignal signal
    ) {
        if (base == null) {
            throw new IllegalArgumentException("base snapshot is required");
        }

        if (signal == null) {
            throw new IllegalArgumentException("message signal is required");
        }

        RecoveryState baseState = base.state();

        int urgeLogs = safe(baseState == null ? 0 : baseState.urgeLogs())
                + safe(signal.urgeLogDelta());

        int betAttempts = safe(baseState == null ? 0 : baseState.betAttempts())
                + safe(signal.betAttemptDelta());

        int betBlockedCount = safe(baseState == null ? 0 : baseState.betBlockedCount())
                + safe(signal.betBlockedDelta());

        int recoveryActionCount = safe(baseState == null ? 0 : baseState.recoveryActionCount())
                + safe(signal.recoveryActionDelta());

        int relapseSignalCount = safe(baseState == null ? 0 : baseState.relapseSignalCount())
                + safe(signal.relapseSignalDelta());

        int eventsCount = safe(baseState == null ? 0 : baseState.eventsCount())
                + signalEventCount(signal);

        RecoveryState projectedState = new RecoveryState(
                urgeLogs,
                betAttempts,
                betBlockedCount,
                recoveryActionCount,
                relapseSignalCount,
                eventsCount
        );

        return new RecoverySnapshot(
                base.ymd(),
                projectedState,
                eventsCount > 0,
                base.streakDays(),
                base.lastEventAt(),
                base.daysSinceLastEvent(),
                base.recentEventCount3d() + signalEventCount(signal),
                base.recentKnowledgeId()
        );
    }

    private int signalEventCount(RecoveryMessageSignal signal) {
        return safe(signal.urgeLogDelta())
                + safe(signal.betAttemptDelta())
                + safe(signal.betBlockedDelta())
                + safe(signal.recoveryActionDelta())
                + safe(signal.relapseSignalDelta());
    }

    private int safe(int value) {
        return Math.max(0, value);
    }
}