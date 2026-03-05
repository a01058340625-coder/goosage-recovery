package com.goosage.api.view.study;

/**
 * Prediction 계산에 필요한 입력값 묶음
 * - Evidence/Rules에서 요구하는 accessor 메서드(streakDays 등)를 제공해야 함
 */
public record PredictionInput(
        long userId,
        int streakDays,
        int daysSinceLastEvent,
        int recentEventCount3d
) {
    public static PredictionInput of(long userId, int streakDays, int daysSinceLastEvent, int recentEventCount3d) {
        return new PredictionInput(userId, streakDays, daysSinceLastEvent, recentEventCount3d);
    }
}
