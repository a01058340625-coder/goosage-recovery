package com.goosage.service.study.predict.rule;

import org.springframework.stereotype.Component;

import com.goosage.service.study.predict.model.Prediction;
import com.goosage.service.study.predict.model.PredictionEvidence;
import com.goosage.service.study.predict.model.PredictionInput;
import com.goosage.service.study.predict.model.PredictionLevel;
import com.goosage.service.study.predict.model.PredictionReasonCode;

@Component
public class HabitCollapseRiskRule implements PredictionRule {

    @Override
    public boolean matches(PredictionInput i) {
        return i.daysSinceLastEvent() >= 3;
    }

    @Override
    public Prediction predict(PredictionInput i) {
        return Prediction.of(
            PredictionLevel.RISK,
            PredictionReasonCode.GAP_4DAYS,
            PredictionEvidence.from(i)
        );
    }


    @Override
    public int priority() {
        return 10;
    }
}
