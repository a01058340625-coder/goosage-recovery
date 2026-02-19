package com.goosage.domain.predict.rules;

import java.util.Map;

import com.goosage.domain.predict.*;
import com.goosage.domain.study.StudySnapshot;

public class LowActivity3dRule implements PredictionRule {

    @Override public int priority() { return 30; }

    @Override
    public boolean matches(StudySnapshot s) {
        return !s.studiedToday() && s.recentEventCount3d() <= 1;
    }

    @Override
    public Prediction apply(StudySnapshot s) {
        return new Prediction(
            PredictionLevel.WARNING,
            PredictionReasonCode.LOW_ACTIVITY_3D,
            Map.of(
                "recentEventCount3d", s.recentEventCount3d(),
                "daysSinceLastEvent", s.daysSinceLastEvent()
            )
        );
    }
}
