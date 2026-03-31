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
    public int priority() {
        return 40;
    }

    @Override
    public boolean matches(StudySnapshot s) {
        if (s == null) {
            return false;
        }

        if (s.studiedToday()) {
            return false;
        }

        int daysSince = s.daysSinceLastEvent();
        int recent3d = s.recentEventCount3d();

        return daysSince >= 2 && recent3d == 0;
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
                        "streakDays", s.streakDays(),
                        "eventsCount", s.state() != null ? s.state().eventsCount() : 0
                )
        );
    }
}