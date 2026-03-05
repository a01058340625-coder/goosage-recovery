package com.goosage.domain.study;

import com.goosage.domain.NextActionType;
import com.goosage.domain.predict.Prediction;

public record StudyCoachResult(
        StudyState state,
        Prediction prediction,
        NextActionType nextAction
) {}