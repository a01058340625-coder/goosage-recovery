package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.recovery.RecoverySnapshot;

@Component
public class StableProgressRule implements PredictionRule {

    @Override
    public int priority() {
    	return 55;
    }

    @Override
    public boolean matches(RecoverySnapshot s) {
        if (s == null || s.state() == null) {
            return false;
        }

        if (s.state().eventsCount() < 4) {
            return false;
        }

        double open = s.openRatio();
        double quiz = s.quizRatio();
        double wrong = s.wrongRatio();
        double done = s.wrongDoneRatio();

        return open >= 0.1 && open <= 0.4
                && quiz >= 0.2 && quiz <= 0.5
                && wrong <= 0.4
                && done <= 0.4;
    }

    @Override
    public Prediction apply(RecoverySnapshot s) {
        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.STABLE_PROGRESS,
                "행동 흐름은 비교적 균형적이다. 지금 리듬을 유지하며 퀴즈를 조금 더 늘리자.",
                Map.of(
                        "openRatio", s.openRatio(),
                        "quizRatio", s.quizRatio(),
                        "wrongRatio", s.wrongRatio(),
                        "wrongDoneRatio", s.wrongDoneRatio(),
                        "eventsCount", s.state().eventsCount()
                )
        );
    }
}