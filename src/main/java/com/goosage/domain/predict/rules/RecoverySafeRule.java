package com.goosage.domain.predict.rules;

import java.util.Map;
import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.study.StudySnapshot;

@Component
public class RecoverySafeRule implements PredictionRule {

    @Override
    public int priority() {
        return 80;
    }

    @Override
    public boolean matches(StudySnapshot s) {
        return s.state() != null
                && s.state().wrongReviewDoneCount() > s.state().wrongReviews();
    }

    @Override
    public Prediction apply(StudySnapshot s) {
        return Prediction.of(
                PredictionLevel.SAFE,
                PredictionReasonCode.RECOVERY_SAFE,
                "복습 완료가 누적되어 회복 안정권에 들어왔다.",
                Map.of(
                        "wrongReviews", s.state().wrongReviews(),
                        "wrongReviewDoneCount", s.state().wrongReviewDoneCount(),
                        "eventsCount", s.state().eventsCount(),
                        "daysSinceLastEvent", s.daysSinceLastEvent()
                )
        );
    }
}