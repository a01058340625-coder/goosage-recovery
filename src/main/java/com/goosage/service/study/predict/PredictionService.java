package com.goosage.service.study.predict;

import com.goosage.service.study.predict.model.Prediction;
import com.goosage.service.study.predict.model.PredictionInput;

public interface PredictionService {
    Prediction predict(PredictionInput input);
}
