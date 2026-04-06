package com.goosage.infra.adapter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.goosage.app.predict.PredictionService;
import com.goosage.app.recovery.action.NextActionService;
import com.goosage.domain.NextActionType;
import com.goosage.domain.predict.Prediction;
import com.goosage.domain.recovery.RecoveryCoachPort;
import com.goosage.domain.recovery.RecoveryCoachResult;
import com.goosage.domain.recovery.RecoverySnapshot;
import com.goosage.domain.recovery.RecoverySnapshotService;
import com.goosage.domain.recovery.RecoveryState;

@Component
public class RecoveryCoachAdapter implements RecoveryCoachPort {

    private final RecoverySnapshotService recoverySnapshotService;
    private final NextActionService nextActionService;
    private final PredictionService predictionService;

    public RecoveryCoachAdapter(
            RecoverySnapshotService recoverySnapshotService,
            NextActionService nextActionService,
            PredictionService predictionService
    ) {
        this.recoverySnapshotService = recoverySnapshotService;
        this.nextActionService = nextActionService;
        this.predictionService = predictionService;
    }

    @Override
    public RecoveryCoachResult execute(long userId) {

        RecoverySnapshot snap = recoverySnapshotService.snapshot(
                userId,
                LocalDate.now(),
                LocalDateTime.now()
        );
        RecoveryState state = snap.state();

        Prediction prediction = predictionService.predict(snap);
        NextActionType next = nextActionService.decide(snap, prediction.reasonCode());

        return new RecoveryCoachResult(state, prediction, next);
    }
}