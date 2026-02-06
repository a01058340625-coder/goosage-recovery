package com.goosage.service.study.predict;

import com.goosage.service.study.predict.model.*;

public class ExposurePolicy {

    public static ExposureLevel decide(Prediction p) {
        if (p == null) return ExposureLevel.SUMMARY;
        if (p.isDataPoor()) return ExposureLevel.SUMMARY;

        return switch (p.level()) {
            case SAFE -> ExposureLevel.SUMMARY;
            case RISK -> ExposureLevel.DETAIL;
        };
    }
}
