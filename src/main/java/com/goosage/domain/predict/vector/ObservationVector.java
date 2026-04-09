package com.goosage.domain.predict.vector;

public record ObservationVector(
        double activity,
        double urgeRatio,
        double attemptRatio,
        double blockedRatio,
        double recoveryRatio,
        double relapseRatio,
        double recentScore,
        double streakScore,
        double recencyPenalty
) {
}