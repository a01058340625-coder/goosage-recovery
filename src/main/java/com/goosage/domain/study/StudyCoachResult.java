package com.goosage.domain.study;

import com.goosage.infra.service.study.action.NextActionDto;
import com.goosage.infra.service.study.predict.model.InfraPredictionView;

public record StudyCoachResult(
        StudyState state,
        NextActionDto nextAction,
        InfraPredictionView prediction
) {}
