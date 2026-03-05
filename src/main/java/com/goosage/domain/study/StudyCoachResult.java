package com.goosage.domain.study;

import com.goosage.api.view.study.CoachPredictionView;
import com.goosage.api.view.study.NextActionDto;

public record StudyCoachResult(
        StudyState state,
        NextActionDto nextAction,
        CoachPredictionView prediction
) {}
