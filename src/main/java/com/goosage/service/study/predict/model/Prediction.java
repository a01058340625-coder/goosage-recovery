package com.goosage.service.study.predict.model;

public record Prediction(
        PredictionLevel level,
        PredictionReasonCode reasonCode,
        PredictionEvidence evidence
) {

    public static Prediction of(PredictionLevel level, PredictionReasonCode reasonCode, PredictionEvidence evidence) {
        return new Prediction(level, reasonCode, evidence);
    }

    public boolean isDataPoor() {
        return reasonCode == PredictionReasonCode.DATA_POOR;
    }
}
