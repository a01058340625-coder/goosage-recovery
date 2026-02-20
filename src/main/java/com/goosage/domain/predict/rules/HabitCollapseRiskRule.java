package com.goosage.domain.predict.rules;

import java.util.Map;

import com.goosage.domain.predict.*;
import com.goosage.domain.study.StudySnapshot;
import org.springframework.stereotype.Component;

@Component

public class HabitCollapseRiskRule implements PredictionRule {

    @Override public int priority() { return 10; }

    @Override
    public boolean matches(StudySnapshot s) {
        return !s.studiedToday() && s.daysSinceLastEvent() >= 3;
    }

    @Override
    public Prediction apply(StudySnapshot s) {
        var level = (s.daysSinceLastEvent() >= 4) ? PredictionLevel.DANGER : PredictionLevel.WARNING;

        return Prediction.of(
            level,
            PredictionReasonCode.LOW_ACTIVITY_3D, // ✅ enum에 있는 값으로 통일
            "학습 공백이 길어지고 있어. 오늘 최소 1개 이벤트로 흐름을 되살리자.",
            Map.of(
                "studiedToday", s.studiedToday(),
                "daysSinceLastEvent", s.daysSinceLastEvent(),
                "recentEventCount3d", s.recentEventCount3d(),
                "streakDays", s.streakDays()
            )
        );
    }
}