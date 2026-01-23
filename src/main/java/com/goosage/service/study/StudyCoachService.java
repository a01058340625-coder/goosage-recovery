package com.goosage.service.study;

import com.goosage.service.study.action.NextActionDto;
import com.goosage.service.study.action.NextActionService;
import com.goosage.service.study.dto.StudyCoachResponse;
import com.goosage.service.study.dto.StudyStateDto;
import com.goosage.service.study.interpret.StudyInterpretationService;
import org.springframework.stereotype.Service;

@Service
public class StudyCoachService {

    private final StudyStateService studyStateService;
    private final StudyInterpretationService interpretationService;
    private final NextActionService nextActionService;

    public StudyCoachService(
            StudyStateService studyStateService,
            StudyInterpretationService interpretationService,
            NextActionService nextActionService
    ) {
        this.studyStateService = studyStateService;
        this.interpretationService = interpretationService;
        this.nextActionService = nextActionService;
    }

    public StudyCoachResponse coach(long userId) {
        StudyStateDto state = studyStateService.getState(userId);
        String interpretation = interpretationService.interpret(state);
        NextActionDto nextAction = nextActionService.decide(state);

        return new StudyCoachResponse(state, interpretation, nextAction);
    }
}
