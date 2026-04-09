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

        double urge = s.urgeRatio();
        double attempt = s.attemptRatio();
        double blocked = s.blockedRatio();
        double recovery = s.recoveryRatio();
        double relapse = s.relapseRatio();

        return urge >= 0.1 && urge <= 0.5
                && attempt >= 0.1 && attempt <= 0.5
                && blocked <= 0.5
                && recovery <= 0.6
                && relapse <= 0.3;
    }

    @Override
    public Prediction apply(RecoverySnapshot s) {
        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.STABLE_PROGRESS,
                "행동 흐름은 비교적 균형적이다. 지금 리듬을 유지하며 회복 행동을 조금 더 늘리자.",
                Map.of(
                        "urgeRatio", s.urgeRatio(),
                        "attemptRatio", s.attemptRatio(),
                        "blockedRatio", s.blockedRatio(),
                        "recoveryRatio", s.recoveryRatio(),
                        "relapseRatio", s.relapseRatio(),
                        "eventsCount", s.state().eventsCount()
                )
        );
    }
}