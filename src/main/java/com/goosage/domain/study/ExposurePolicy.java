package com.goosage.domain.study;

import com.goosage.infra.service.study.predict.model.ExposureLevel;
import com.goosage.infra.service.study.predict.model.Prediction;

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
