package com.goosage.infra.adapter;

import org.springframework.stereotype.Component;

import com.goosage.domain.study.StudySnapshot;
import com.goosage.domain.study.StudyState;
import com.goosage.domain.study.StudyCoachPort;
import com.goosage.domain.study.StudyCoachResult;
import com.goosage.infra.service.study.action.NextActionDto;
import com.goosage.infra.service.study.action.NextActionService;
import com.goosage.infra.service.study.interpret.StudyInterpretationService;
import com.goosage.infra.service.study.predict.PredictionService;
import com.goosage.infra.service.study.predict.model.Prediction;
import com.goosage.infra.service.study.predict.model.PredictionInput;
import com.goosage.infra.service.study.mapper.PredictionMappers;

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

        // 1) 단일 출처 Snapshot
        StudySnapshot snap = interpretationService.getSnapshot(userId);

        // 2) 엔진 단일 진실
        StudyState state = snap.state();

        // ✅ 3) 결정은 snapshot 단일 입력 (context 포함)
        Prediction prediction = predictionService.predict(snap);
        NextActionDto next = nextActionService.decide(snap, prediction.reasonCode());

        return new StudyCoachResult(state, next, prediction);
    }
}

