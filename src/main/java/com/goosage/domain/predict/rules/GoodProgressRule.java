package com.goosage.domain.predict.rules;

import java.util.Map;
import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.study.StudySnapshot;

@Component
public class GoodProgressRule implements PredictionRule {

    @Override
    public int priority() {
        return 60;
    }

    @Override
    public boolean matches(StudySnapshot s) {
        return s.studiedToday()
                && s.state().eventsCount() >= 5
                && s.quizRatio() >= 0.5
                && s.state().wrongReviews() == 0;
    }

    @Override
    public Prediction apply(StudySnapshot s) {
        return Prediction.of(
                PredictionLevel.SAFE,
                PredictionReasonCode.GOOD_PROGRESS,
                "퀴즈 중심으로 안정적으로 진행 중이다. 지금 흐름을 유지하자.",
                Map.of(
                        "quizRatio", s.quizRatio(),
                        "eventsCount", s.state().eventsCount(),
                        "quizSubmits", s.state().quizSubmits(),
                        "wrongReviews", s.state().wrongReviews()
                )
        );
    }
}