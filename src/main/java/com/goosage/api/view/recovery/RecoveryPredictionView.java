package com.goosage.api.view.recovery;

// v1.4 계약(View) - 4필드 고정
public record RecoveryPredictionView(
        String level,
        String expectedOutcome,
        String reason,
        String minimalAction
) {}