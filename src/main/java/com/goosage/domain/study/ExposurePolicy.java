package com.goosage.domain.study;

import com.goosage.infra.service.study.predict.model.ExposureLevel;
import com.goosage.infra.service.study.predict.model.InfraPredictionView;

public class ExposurePolicy {

    public static ExposureLevel decide(InfraPredictionView p) {
        if (p == null) return ExposureLevel.SUMMARY;
        if (p.isDataPoor()) return ExposureLevel.SUMMARY;

        return switch (p.level()) {
            case SAFE -> ExposureLevel.SUMMARY;
            case RISK -> ExposureLevel.DETAIL;
        };
    }
}
