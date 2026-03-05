package com.goosage.domain.study;

import com.goosage.api.view.study.InfraPredictionView;
import com.goosage.api.view.study.NextActionDto;

public record StudyCoachResult(
        StudyState state,
        NextActionDto nextAction,
        InfraPredictionView prediction
) {}
