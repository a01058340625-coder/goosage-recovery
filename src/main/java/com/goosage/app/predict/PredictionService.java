package com.goosage.app.predict;

import com.goosage.api.view.study.CoachPredictionView;
import com.goosage.api.view.study.PredictionInput;
import com.goosage.domain.study.StudySnapshot;
import com.goosage.domain.study.StudyState;

public interface PredictionService {
    CoachPredictionView predict(PredictionInput input);  // (임시/삭제대상)
    CoachPredictionView predict(StudyState state);       // (임시/삭제대상)
    CoachPredictionView predict(StudySnapshot snapshot); // ✅ v1.5 SSOT 계약
}