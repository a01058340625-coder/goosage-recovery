package com.goosage.api.view.study;

import com.goosage.api.view.study.ExposureLevel;
import com.goosage.api.view.study.CoachPredictionView;

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
