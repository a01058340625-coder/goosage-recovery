package com.goosage.app.predict;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.study.StudySnapshot;

public interface PredictionService {
    Prediction predict(StudySnapshot snapshot); // ✅ SSOT 계약
}