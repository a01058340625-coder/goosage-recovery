package com.goosage.service.study.dto;

import com.goosage.service.study.action.NextActionDto;

public record StudyCoachResponse(
        StudyStateDto state,
        String interpretation,
        NextActionDto nextAction
) {}
