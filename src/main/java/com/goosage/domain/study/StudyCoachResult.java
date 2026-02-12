package com.goosage.domain.study;

import com.goosage.infra.service.study.action.NextActionDto;
import com.goosage.infra.service.study.predict.model.Prediction;

public record StudyCoachResult(
        NextActionDto nextAction,
        Prediction prediction
) {}
