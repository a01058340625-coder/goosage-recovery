package com.goosage.api.view.recovery;

public record RecoveryCoachResponse(
        RecoveryStateView state,
        NextActionView next,
        RecoveryPredictionView prediction
) {}
