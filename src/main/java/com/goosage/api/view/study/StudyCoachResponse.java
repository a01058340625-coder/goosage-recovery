package com.goosage.api.view.study;

import com.goosage.dto.study.NextActionView;
import com.goosage.dto.study.StudyStateView;

public record StudyCoachResponse(
        StudyStateView state,
        NextActionView next,
        PredictionDto prediction
) {}
