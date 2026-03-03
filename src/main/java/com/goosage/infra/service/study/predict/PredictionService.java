package com.goosage.infra.service.study.predict;

import com.goosage.domain.study.StudySnapshot;
import com.goosage.domain.study.StudyState;
import com.goosage.infra.service.study.predict.model.InfraPredictionView;
import com.goosage.infra.service.study.predict.model.PredictionInput;

public interface PredictionService {
    InfraPredictionView predict(PredictionInput input);  // (임시/삭제대상)
    InfraPredictionView predict(StudyState state);       // (임시/삭제대상)
    InfraPredictionView predict(StudySnapshot snapshot); // ✅ v1.5 SSOT 계약
}