package com.goosage.api.view.study;

public record CoachPredictionView(
        CoachPredictionLevel level,
        CoachPredictionReasonCode reasonCode,
        PredictionEvidence evidence
) {

    public static CoachPredictionView of(CoachPredictionLevel level, CoachPredictionReasonCode reasonCode, PredictionEvidence evidence) {
        return new CoachPredictionView(level, reasonCode, evidence);
    }

    public boolean isDataPoor() {
        return reasonCode == CoachPredictionReasonCode.DATA_POOR;
    }
}
