package com.goosage.api.view.study;

public record PredictionDto(
        String level,          // SAFE / WARNING / RISK
        String expectedOutcome,
        String reason,
        String minimalAction
) {}
