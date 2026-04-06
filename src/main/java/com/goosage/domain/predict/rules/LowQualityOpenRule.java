package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.recovery.RecoverySnapshot;

@Component
public class LowQualityOpenRule implements PredictionRule {

    private static final int EVENTS_MIN = 5;
    private static final double OPEN_RATIO_MIN = 0.55;
    private static final double QUIZ_RATIO_MAX = 0.45;

    @Override
    public int priority() {
        return 20;
    }

    @Override
    public boolean matches(RecoverySnapshot s) {
        if (s == null || s.state() == null) {
            return false;
        }

        if (!s.studiedToday()) {
            return false;
        }

        int events = s.state().eventsCount();

        double openRatio = s.openRatio();
        double quizRatio = s.quizRatio();

        return events >= EVENTS_MIN
                && openRatio >= OPEN_RATIO_MIN
                && quizRatio <= QUIZ_RATIO_MAX;
    }

    @Override
    public Prediction apply(RecoverySnapshot s) {
        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.LOW_QUALITY_OPEN,
                "열기 행동 비중이 너무 높아 실제 학습 품질이 떨어지고 있어. 퀴즈 1개로 흐름을 바로잡자.",
                Map.of(
                        "openRatio", s.openRatio(),
                        "quizRatio", s.quizRatio(),
                        "eventsCount", s.state().eventsCount(),
                        "studiedToday", s.studiedToday(),
                        "eventsMin", EVENTS_MIN,
                        "openRatioMin", OPEN_RATIO_MIN,
                        "quizRatioMax", QUIZ_RATIO_MAX
                )
        );
    }
}