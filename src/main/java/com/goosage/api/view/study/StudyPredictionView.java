package com.goosage.api.view.study;

public record StudyPredictionView(
        String level,
        String expectedOutcome,
        String reason,
        String minimalAction
) {}
