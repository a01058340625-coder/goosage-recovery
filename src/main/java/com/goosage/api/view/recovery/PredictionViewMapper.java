package com.goosage.api.view.recovery;

import org.springframework.stereotype.Component;

import com.goosage.app.predict.PredictionCopyService;

@Component
public class PredictionViewMapper {

    private final PredictionCopyService copyService;

    public PredictionViewMapper(PredictionCopyService copyService) {
        this.copyService = copyService;
    }

    // CoachPredictionView -> RecoveryPredictionView
    public RecoveryPredictionView toView(CoachPredictionView p) {
        var copy = copyService.render(p);

        ExposureLevel exposureLevel = ExposurePolicy.decide(p);

        String reason =
                exposureLevel == ExposureLevel.DETAIL
                        ? p.reasonCode().description()
                        : "";

        return new RecoveryPredictionView(
                p.level().name(),
                copy.expectedOutcome(),
                reason,
                copy.minimalAction()
        );
    }

    public RecoveryPredictionView toDto(CoachPredictionView p) {
        return toView(p);
    }
}