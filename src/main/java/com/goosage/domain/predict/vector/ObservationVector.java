package com.goosage.domain.predict.vector;

public record ObservationVector(
        double activity,
        double openRatio,
        double quizRatio,
        double wrongRatio,
        double wrongDoneRatio,
        double recentScore,
        double streakScore,
        double recencyPenalty
) {
}