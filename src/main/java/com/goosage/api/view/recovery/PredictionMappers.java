package com.goosage.api.view.recovery;

import com.goosage.domain.recovery.RecoverySnapshot;

public final class PredictionMappers {

    private PredictionMappers() {}

    public static PredictionInput toPredictionInput(long userId, RecoverySnapshot s) {
        if (s == null) {
            return PredictionInput.of(userId, 0, 9999, 0);
        }
        return PredictionInput.of(
                userId,
                s.streakDays(),
                s.daysSinceLastEvent(),
                s.recentEventCount3d()
        );
    }
}
