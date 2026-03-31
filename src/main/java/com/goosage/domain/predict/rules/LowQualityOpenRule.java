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
        return 20;
    }

    @Override
    public boolean matches(StudySnapshot s) {
        if (s == null || s.state() == null) {
            return false;
        }

        if (!s.studiedToday()) {
            return false;
        }

        int events = s.state().eventsCount();
        int quiz = s.state().quizSubmits();

        return events >= 5 && quiz == 0;
    }

    @Override
    public Prediction apply(StudySnapshot s) {
        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.LOW_QUALITY_OPEN,
                "열기 행동만 반복되고 있어. 퀴즈 1개로 실제 학습으로 전환하자.",
                Map.of(
                        "openRatio", s.openRatio(),
                        "quizRatio", s.quizRatio(),
                        "eventsCount", s.state().eventsCount(),
                        "studiedToday", s.studiedToday()
                )
        );
    }
}