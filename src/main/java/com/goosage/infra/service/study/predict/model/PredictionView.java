package com.goosage.infra.service.study.predict.model;

import java.util.List;

public record PredictionView(
        String copy,
        ExposureLevel exposureLevel,
        List<EvidenceView> evidences,
        List<String> reasons
) {}
