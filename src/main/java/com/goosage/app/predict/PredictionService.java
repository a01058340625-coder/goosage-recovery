package com.goosage.app.predict;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.recovery.RecoverySnapshot;

public interface PredictionService {
    Prediction predict(RecoverySnapshot snapshot); // ✅ SSOT 계약
}