package com.goosage.infra.adapter;

import org.springframework.stereotype.Component;

import com.goosage.domain.study.StudyCoachPort;
import com.goosage.domain.study.StudyCoachResult;
import com.goosage.infra.service.study.action.NextActionDto;
import com.goosage.infra.service.study.action.NextActionService;
import com.goosage.infra.service.study.dto.StudyStateDto;
import com.goosage.infra.service.study.interpret.StudyInterpretationService;
import com.goosage.infra.service.study.predict.PredictionService;
import com.goosage.infra.service.study.predict.model.Prediction;
import com.goosage.infra.service.study.predict.model.PredictionInput;

@Component
public class StudyCoachAdapter implements StudyCoachPort {

    private final StudyInterpretationService interpretationService;
    private final NextActionService nextActionService;
    private final PredictionService predictionService;

    public StudyCoachAdapter(
            StudyInterpretationService interpretationService,
            NextActionService nextActionService,
            PredictionService predictionService
    ) {
        this.interpretationService = interpretationService;
        this.nextActionService = nextActionService;
        this.predictionService = predictionService;
    }

    @Override
    public StudyCoachResult execute(long userId) {

        // ✅ 너의 단일 진실: getState(Long)
        StudyStateDto state = interpretationService.getState(userId);

        // ✅ decide(StudyStateDto) 계약 그대로
        NextActionDto next = nextActionService.decide(state);

        // ✅ predict(PredictionInput) 계약 그대로
        PredictionInput input = PredictionInput.of(userId, 0, 0, 0);
        Prediction prediction = predictionService.predict(input);

        return new StudyCoachResult(next, prediction);
    }
}
