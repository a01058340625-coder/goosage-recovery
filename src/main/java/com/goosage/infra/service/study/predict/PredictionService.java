package com.goosage.infra.service.study.predict;

import com.goosage.infra.service.study.predict.model.Prediction;
import com.goosage.infra.service.study.predict.model.PredictionInput;

public interface PredictionService {
    Prediction predict(PredictionInput input);
}
