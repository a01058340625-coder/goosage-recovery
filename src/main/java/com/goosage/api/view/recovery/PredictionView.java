package com.goosage.api.view.recovery;

import java.util.List;

public record PredictionView(
        String copy,
        ExposureLevel exposureLevel,
        List<EvidenceView> evidences,
        List<String> reasons
) {}
