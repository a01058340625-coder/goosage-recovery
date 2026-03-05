package com.goosage.api.view.study;

// v1.4 계약(View) - 4필드 고정
public record StudyPredictionView(
        String level,
        String expectedOutcome,
        String reason,
        String minimalAction
) {}