package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.recovery.RecoverySnapshot;

@Component
public class ReviewWrongNeededRule implements PredictionRule {

    private static final int ACTION_MIN = 1;

    @Override
    public int priority() {
        return 10;
    }

    @Override
    public boolean matches(RecoverySnapshot s) {
        if (s == null || s.state() == null) {
            return false;
        }

        if (!s.studiedToday()) {
            return false;
        }

        int risk = s.state().wrongReviews();
        int recovered = s.state().wrongReviewDoneCount();

        return risk > 0 && recovered == 0;
    }

    @Override
    public Prediction apply(RecoverySnapshot s) {
        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.REVIEW_WRONG_PENDING,
                "위험 신호가 남아 있어. 하나씩 회복 행동으로 정리하자.",
                Map.of(
                        "studiedToday", s.studiedToday(),
                        "streakDays", s.streakDays(),
                        "daysSinceLastEvent", s.daysSinceLastEvent(),
                        "recentEventCount3d", s.recentEventCount3d(),
                        "eventsCount", s.state().eventsCount(),
                        "actionCount", s.state().quizSubmits(),
                        "riskSignal", s.state().wrongReviews(),
                        "recoveryAction", s.state().wrongReviewDoneCount()
                )
        );
    }
}