package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.study.StudySnapshot;

@Component
public class WrongHeavyRule implements PredictionRule {

    @Override
    public int priority() {
        return 45;
    }

    @Override
    public boolean matches(StudySnapshot s) {
        if (s == null || s.state() == null) {
            return false;
        }

        if (s.state().eventsCount() < 5) {
            return false;
        }

        return s.wrongRatio() >= 0.5;
    }

    @Override
    public Prediction apply(StudySnapshot s) {
        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.WRONG_HEAVY,
                "틀린 문제 비율이 높다. 새 학습보다 틀린 문제 복습을 먼저 하자.",
                Map.of(
                        "wrongRatio", s.wrongRatio(),
                        "wrongReviews", s.state().wrongReviews(),
                        "wrongReviewDoneCount", s.state().wrongReviewDoneCount(),
                        "eventsCount", s.state().eventsCount(),
                        "quizRatio", s.quizRatio()
                )
        );
    }
}