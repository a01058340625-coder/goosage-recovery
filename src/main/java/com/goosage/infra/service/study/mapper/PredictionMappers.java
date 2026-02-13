package com.goosage.infra.service.study.mapper;

import com.goosage.domain.study.StudySnapshot;
import com.goosage.infra.service.study.predict.model.PredictionInput;

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
