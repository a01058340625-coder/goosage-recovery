package com.goosage.api.view.study;

public record InfraPredictionView(
        InfraPredictionLevelView level,
        InfraPredictionReasonCodeView reasonCode,
        PredictionEvidence evidence
) {

    public static InfraPredictionView of(InfraPredictionLevelView level, InfraPredictionReasonCodeView reasonCode, PredictionEvidence evidence) {
        return new InfraPredictionView(level, reasonCode, evidence);
    }

    public boolean isDataPoor() {
        return reasonCode == InfraPredictionReasonCodeView.DATA_POOR;
    }
}
