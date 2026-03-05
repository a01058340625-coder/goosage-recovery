package com.goosage.infra.adapter;

import org.springframework.stereotype.Component;

import com.goosage.app.predict.PredictionService;
import com.goosage.app.study.action.NextActionService;
import com.goosage.app.study.interpret.StudyInterpretationService;
import com.goosage.domain.NextActionType;
import com.goosage.domain.predict.Prediction;
import com.goosage.domain.study.StudyCoachPort;
import com.goosage.domain.study.StudyCoachResult;
import com.goosage.domain.study.StudySnapshot;
import com.goosage.domain.study.StudyState;

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

        StudySnapshot snap = interpretationService.getSnapshot(userId);
        StudyState state = snap.state();

        Prediction prediction = predictionService.predict(snap); // ✅ 도메인
        NextActionType next = nextActionService.decide(snap, prediction.reasonCode()); // ✅ 도메인 enum

        return new StudyCoachResult(state, prediction, next); // (레코드 필드 순서에 맞춰)
    }
}