package com.goosage.domain.study;

import org.springframework.stereotype.Component;

import com.goosage.infra.service.study.predict.model.Prediction;
import com.goosage.infra.service.study.predict.model.PredictionEvidence;
import com.goosage.infra.service.study.predict.model.PredictionInput;
import com.goosage.infra.service.study.predict.model.PredictionLevel;
import com.goosage.infra.service.study.predict.model.PredictionReasonCode;

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
