package com.goosage.service.study;

import org.springframework.stereotype.Service;

import com.goosage.dto.study.PredictionDto;
import com.goosage.dto.study.StudyCoachResponse;
import com.goosage.service.study.action.NextActionDto;
import com.goosage.service.study.action.NextActionService;
import com.goosage.service.study.dto.StudyStateDto;
import com.goosage.service.study.interpret.StudyInterpretationService;

@Service
public class StudyCoachService {

    private final StudyInterpretationService studyInterpretationService;
    private final NextActionService nextActionService;

    public StudyCoachService(
            StudyInterpretationService studyInterpretationService,
            NextActionService nextActionService
    ) {
        this.studyInterpretationService = studyInterpretationService;
        this.nextActionService = nextActionService;
    }

    public StudyCoachResponse coach(Long userId) {

        // ✅ 원래 존재하던 “state 생성 주체”
        StudyStateDto state = studyInterpretationService.getState(userId);

        NextActionDto nextAction = nextActionService.decide(state);

        StudyCoachResponse response = new StudyCoachResponse();
        response.setState(state);
        response.setNextAction(nextAction);
        response.setPrediction(defaultPrediction());

        return response;
    }

    private PredictionDto defaultPrediction() {
        return new PredictionDto("UNCERTAIN", "SHORT", "rule=baseline");
    }
}
