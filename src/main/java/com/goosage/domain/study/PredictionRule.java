package com.goosage.domain.study;

import com.goosage.infra.service.study.predict.model.Prediction;
import com.goosage.infra.service.study.predict.model.PredictionInput;

public interface PredictionRule {
    boolean matches(PredictionInput input);
    Prediction predict(PredictionInput input);

    /**
     * 낮을수록 먼저 적용.
     * (RISK가 SAFE보다 우선해야 하므로 보통 RISK가 더 낮은 값)
     */
    int priority();
}
