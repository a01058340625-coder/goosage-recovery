package com.goosage.domain.predict;

import java.util.Map;

public record Prediction(
    PredictionLevel level,
    PredictionReasonCode reasonCode,
    Map<String, Object> evidence
) {}
