package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.study.StudySnapshot;

@Component
public class HabitCollapseRiskRule implements PredictionRule {

    @Override
    public int priority() { return 15; }

    @Override
    public boolean matches(StudySnapshot s) {
        // 오늘 안했고, 마지막 이벤트가 3일 이상 전
        return !s.studiedToday() && s.daysSinceLastEvent() >= 3;
    }

    @Override
    public Prediction apply(StudySnapshot s) {
        var level = (s.daysSinceLastEvent() >= 4)
                ? PredictionLevel.DANGER
                : PredictionLevel.WARNING;

        return Prediction.of(
                level,
                PredictionReasonCode.HABIT_COLLAPSE,
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