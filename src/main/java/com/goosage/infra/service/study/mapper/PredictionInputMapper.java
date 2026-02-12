package com.goosage.infra.service.study.mapper;

import com.goosage.domain.study.StudySnapshot;
import com.goosage.infra.service.study.predict.model.PredictionInput;

public class PredictionInputMapper {

    private PredictionInputMapper() {}

    public static PredictionInput from(long userId, StudySnapshot s) {
        return PredictionInput.of(
                userId,
                s.streakDays(),
                s.daysSinceLastEvent(),
                s.recentEventCount3d()
        );
    }
}
