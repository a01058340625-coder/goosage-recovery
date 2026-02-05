package com.goosage.service.study.predict.model;

public record Prediction(
        PredictionLevel level,
        String expectedOutcome,
        String reason,
        String minimalAction
) {}
