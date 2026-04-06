package com.goosage.domain.recovery;

import com.goosage.domain.NextActionType;
import com.goosage.domain.predict.Prediction;

public record RecoveryCoachResult(
        RecoveryState state,
        Prediction prediction,
        NextActionType nextAction
) {}