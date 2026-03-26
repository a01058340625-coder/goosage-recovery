package com.goosage.infra.adapter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.goosage.app.predict.PredictionService;
import com.goosage.app.study.action.NextActionService;
import com.goosage.domain.NextActionType;
import com.goosage.domain.predict.Prediction;
import com.goosage.domain.study.StudyCoachPort;
import com.goosage.domain.study.StudyCoachResult;
import com.goosage.domain.study.StudySnapshot;
import com.goosage.domain.study.StudySnapshotService;
import com.goosage.domain.study.StudyState;

@Component
public class StudyCoachAdapter implements StudyCoachPort {

    private final StudySnapshotService studySnapshotService;
    private final NextActionService nextActionService;
    private final PredictionService predictionService;

    public StudyCoachAdapter(
            StudySnapshotService studySnapshotService,
            NextActionService nextActionService,
            PredictionService predictionService
    ) {
        this.studySnapshotService = studySnapshotService;
        this.nextActionService = nextActionService;
        this.predictionService = predictionService;
    }

    @Override
    public StudyCoachResult execute(long userId) {

        StudySnapshot snap = studySnapshotService.snapshot(
                userId,
                LocalDate.now(),
                LocalDateTime.now()
        );
        StudyState state = snap.state();

        Prediction prediction = predictionService.predict(snap);
        NextActionType next = nextActionService.decide(snap, prediction.reasonCode());

        return new StudyCoachResult(state, prediction, next);
    }
}