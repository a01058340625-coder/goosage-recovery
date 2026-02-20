package com.goosage.infra.service.study.predict;

import com.goosage.domain.study.StudySnapshot;
import com.goosage.domain.study.StudyState;
import com.goosage.infra.service.study.predict.model.Prediction;
import com.goosage.infra.service.study.predict.model.PredictionInput;

public interface PredictionService {
    Prediction predict(PredictionInput input);  // (임시/삭제대상)
    Prediction predict(StudyState state);       // (임시/삭제대상)
    Prediction predict(StudySnapshot snapshot); // ✅ v1.5 SSOT 계약
}