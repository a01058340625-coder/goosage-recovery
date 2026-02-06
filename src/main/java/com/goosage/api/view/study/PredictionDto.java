package com.goosage.api.view.study;

public record PredictionDto(
        String level,
        String expectedOutcome,
        String reason,
        String minimalAction
) {}
