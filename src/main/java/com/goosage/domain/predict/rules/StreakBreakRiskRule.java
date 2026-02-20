package com.goosage.domain.predict.rules;

import java.util.Map;

import com.goosage.domain.predict.*;
import com.goosage.domain.study.StudySnapshot;
import org.springframework.stereotype.Component;

@Component

public class StreakBreakRiskRule implements PredictionRule {

    @Override public int priority() { return 20; }

    @Override
    public boolean matches(StudySnapshot s) {
        return !s.studiedToday()
            && s.streakDays() >= 3
            && s.daysSinceLastEvent() >= 1;
    }

    @Override
    public Prediction apply(StudySnapshot s) {
        return Prediction.of(
            PredictionLevel.WARNING,
            PredictionReasonCode.LOW_ACTIVITY_3D, // ✅ 통일
            "연속 학습 흐름이 끊길 위험이 있어. 오늘 1개만 하면 바로 회복 가능.",
            Map.of(
                "studiedToday", s.studiedToday(),
                "daysSinceLastEvent", s.daysSinceLastEvent(),
                "streakDays", s.streakDays(),
                "recentEventCount3d", s.recentEventCount3d()
            )
        );
    }
}