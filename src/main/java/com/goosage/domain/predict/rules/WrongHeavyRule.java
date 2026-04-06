package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.recovery.RecoverySnapshot;

@Component
public class WrongHeavyRule implements PredictionRule {

    @Override
    public int priority() {
        return 9;
    }

    @Override
    public boolean matches(RecoverySnapshot s) {
        if (s == null || s.state() == null) {
            return false;
        }

        return s.studiedToday()
                && s.state().wrongReviews() >= 3;
    }

    @Override
    public Prediction apply(RecoverySnapshot s) {
        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.WRONG_HEAVY,
                "위험 신호가 많이 쌓였어. 새 행동보다 회복 행동을 먼저 하자.",
                Map.of(
                        "wrongReviews", s.state().wrongReviews(),
                        "wrongReviewDoneCount", s.state().wrongReviewDoneCount(),
                        "eventsCount", s.state().eventsCount(),
                        "quizSubmits", s.state().quizSubmits(),
                        "wrongRatio", s.wrongRatio()
                )
        );
    }
}