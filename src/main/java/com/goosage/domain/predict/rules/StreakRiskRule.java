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

    @Override
    public int priority() {
        return 20;
    }

    @Override
    public boolean matches(StudySnapshot s) {
        return !s.studiedToday()
                && s.daysSinceLastEvent() >= 2
                && s.daysSinceLastEvent() < 4;
    }

    @Override
    public Prediction apply(StudySnapshot s) {
        var level = (s.daysSinceLastEvent() >= 4)
                ? PredictionLevel.DANGER
                : PredictionLevel.WARNING;

        return Prediction.of(
                level,
                PredictionReasonCode.LOW_ACTIVITY_3D,
                "최근 며칠 학습 공백이 이어지고 있다. 오늘 최소 1개 이벤트부터 다시 시작하자.",
                Map.of(
                        "studiedToday", s.studiedToday(),
                        "daysSinceLastEvent", s.daysSinceLastEvent(),
                        "streakDays", s.streakDays(),
                        "recentEventCount3d", s.recentEventCount3d()
                )
        );
    }
}