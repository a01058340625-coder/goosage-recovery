package com.goosage.domain.study;

import org.springframework.stereotype.Component;

import com.goosage.service.study.predict.model.*;

@Component
public class StableStateRule implements PredictionRule {

    @Override
    public boolean matches(PredictionInput i) {
        return i.streakDays() >= 1 && i.daysSinceLastEvent() == 0;
    }

    @Override
    public Prediction predict(PredictionInput i) {
        return Prediction.of(
                PredictionLevel.SAFE,
                PredictionReasonCode.TODAY_DONE,
                PredictionEvidence.from(i)
        );
    }

    @Override
    public int priority() {
        return 30;
    }
}
