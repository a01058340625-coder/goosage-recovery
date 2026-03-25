package com.goosage.domain.predict.rules;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.study.StudySnapshot;

@Component
public class RecoveryProgressRule implements PredictionRule {

    @Override
    public int priority() {
        return 70;
    }

    @Override
    public boolean matches(StudySnapshot s) {
        return s.daysSinceLastEvent() == 0
                && s.state() != null
                && s.state().quizSubmits() == 1
                && s.state().wrongReviews() == 0;
    }

    @Override
    public Prediction apply(StudySnapshot s) {
        return new Prediction(
                PredictionLevel.WARNING,
                PredictionReasonCode.RECOVERY_PROGRESS,
                "복귀는 시작됐어. 퀴즈 1개만 더 해보자.",
                java.util.Map.of(
                        "streakDays", s.streakDays(),
                        "daysSinceLastEvent", s.daysSinceLastEvent(),
                        "recentEventCount3d", s.recentEventCount3d(),
                        "eventsCount", s.state() != null ? s.state().eventsCount() : 0
                )
        );
    }
}