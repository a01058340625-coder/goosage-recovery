package com.goosage.api.view.study;

public record StudyCoachResponse(
        StudyStateView state,
        NextActionView next,
        StudyPredictionView prediction
) {}
