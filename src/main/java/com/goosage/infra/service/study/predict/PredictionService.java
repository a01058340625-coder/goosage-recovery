package com.goosage.infra.service.study.predict;

import com.goosage.domain.study.StudyState;
import com.goosage.infra.service.study.predict.model.Prediction;
import com.goosage.infra.service.study.predict.model.PredictionInput;

public interface PredictionService {

    // 기존 계약 (유지)
    Prediction predict(PredictionInput input);

    // ✅ 새 계약: 단일 진실 입력
    Prediction predict(StudyState state);
}
