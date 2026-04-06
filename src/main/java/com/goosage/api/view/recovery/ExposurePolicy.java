package com.goosage.api.view.recovery;

public class ExposurePolicy {

    public static ExposureLevel decide(CoachPredictionView p) {

        if (p == null) return ExposureLevel.SUMMARY;
        if (p.isDataPoor()) return ExposureLevel.SUMMARY;

        return switch (p.level()) {
            case SAFE -> ExposureLevel.SUMMARY;
            case RISK -> ExposureLevel.DETAIL;
        };
    }
}