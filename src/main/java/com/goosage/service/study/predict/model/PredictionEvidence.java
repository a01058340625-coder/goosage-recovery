package com.goosage.service.study.predict.model;

public record PredictionEvidence(
        int streakDays,
        int daysSinceLastEvent,
        int recentEventCount3d
) {
    public static PredictionEvidence from(PredictionInput i) {
        return new PredictionEvidence(
                i.streakDays(),
                i.daysSinceLastEvent(),
                i.recentEventCount3d()
        );
    }
}
