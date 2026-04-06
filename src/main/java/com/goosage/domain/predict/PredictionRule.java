package com.goosage.domain.predict;

import com.goosage.domain.recovery.RecoverySnapshot;

public interface PredictionRule {
    int priority();
    boolean matches(RecoverySnapshot s);
    Prediction apply(RecoverySnapshot s);
}