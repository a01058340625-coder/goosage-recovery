package com.goosage.domain.predict.rules;

import java.util.Map;

import com.goosage.domain.predict.*;
import com.goosage.domain.study.StudySnapshot;

public class StreakRiskRule implements PredictionRule {

    @Override public int priority() { return 20; }

    @Override
    public boolean matches(StudySnapshot s) {
        return !s.studiedToday() && s.daysSinceLastEvent() >= 2;
    }

    @Override
    public Prediction apply(StudySnapshot s) {
        var level = (s.daysSinceLastEvent() >= 4) ? PredictionLevel.DANGER : PredictionLevel.WARNING;

        return new Prediction(
            level,
            PredictionReasonCode.STREAK_RISK,
            Map.of(
                "studiedToday", s.studiedToday(),
                "daysSinceLastEvent", s.daysSinceLastEvent(),
                "streakDays", s.streakDays()
            )
        );
    }
}
