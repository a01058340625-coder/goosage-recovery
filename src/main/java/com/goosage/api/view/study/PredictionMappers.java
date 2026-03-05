package com.goosage.api.view.study;

import com.goosage.domain.study.StudySnapshot;

public final class PredictionMappers {

    private PredictionMappers() {}

    public static PredictionInput toPredictionInput(long userId, StudySnapshot s) {
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
