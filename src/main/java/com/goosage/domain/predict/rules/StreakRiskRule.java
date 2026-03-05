package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.study.StudySnapshot;

@Component

public class StreakRiskRule implements PredictionRule {

    @Override public int priority() { return 20; }

    @Override
    public boolean matches(StudySnapshot s) {
        return !s.studiedToday() && s.daysSinceLastEvent() >= 2;
    }

    @Override
    public Prediction apply(StudySnapshot s) {
        var level = (s.daysSinceLastEvent() >= 4) ? PredictionLevel.DANGER : PredictionLevel.WARNING;

        return Prediction.of(
            level,
            PredictionReasonCode.LOW_ACTIVITY_3D, // ✅ 통일
            "최근 며칠 학습이 뜸했어. 오늘 최소 1개 이벤트만 만들자.",
            Map.of(
                "studiedToday", s.studiedToday(),
                "daysSinceLastEvent", s.daysSinceLastEvent(),
                "streakDays", s.streakDays(),
                "recentEventCount3d", s.recentEventCount3d()
            )
        );
    }
}