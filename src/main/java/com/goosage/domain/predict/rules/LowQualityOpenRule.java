package com.goosage.domain.predict.rules;

import java.util.Map;
import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.study.StudySnapshot;

@Component
public class LowQualityOpenRule implements PredictionRule {

    @Override
    public int priority() {
        return 40;
    }

    @Override
    public boolean matches(StudySnapshot s) {
        if (s.state() == null) return false;
        if (s.state().eventsCount() < 3) return false;

        return s.openRatio() >= 0.7 && s.quizRatio() <= 0.2;
    }

    @Override
    public Prediction apply(StudySnapshot s) {
        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.LOW_QUALITY_OPEN,
                "열기 행동 비율이 높다. 단순 진입보다 퀴즈나 복습 1개로 전환하자.",
                Map.of(
                        "openRatio", s.openRatio(),
                        "quizRatio", s.quizRatio(),
                        "eventsCount", s.state().eventsCount(),
                        "studiedToday", s.studiedToday()
                )
        );
    }
}