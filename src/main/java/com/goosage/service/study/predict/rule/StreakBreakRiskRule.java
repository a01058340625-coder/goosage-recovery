package com.goosage.service.study.predict.rule;

import org.springframework.stereotype.Component;

import com.goosage.service.study.predict.model.*;

@Component
public class StreakBreakRiskRule implements PredictionRule {

    @Override
    public boolean matches(PredictionInput i) {
        return i.streakDays() >= 3 && i.daysSinceLastEvent() >= 1;
    }

    @Override
    public Prediction predict(PredictionInput i) {
        return Prediction.of(
                PredictionLevel.RISK,
                PredictionReasonCode.GAP_3DAYS,
                PredictionEvidence.from(i)
        );
    }

    @Override
    public int priority() {
        return 20;
    }
}
