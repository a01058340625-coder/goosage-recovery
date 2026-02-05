package com.goosage.service.study.predict.model;

public record PredictionInput(
        int streakDays,
        int daysSinceLastEvent,
        int recentEventCount3d
) {}
