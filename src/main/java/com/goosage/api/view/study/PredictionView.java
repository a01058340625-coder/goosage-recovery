package com.goosage.api.view.study;

import java.util.List;

public record PredictionView(
        String copy,
        ExposureLevel exposureLevel,
        List<EvidenceView> evidences,
        List<String> reasons
) {}
