package com.goosage.api.view.study;

import org.springframework.stereotype.Component;

import com.goosage.app.predict.PredictionCopyService;

@Component
public class PredictionViewMapper {

    private final PredictionCopyService copyService;

    public PredictionViewMapper(PredictionCopyService copyService) {
        this.copyService = copyService;
    }

    // ✅ CoachPredictionView(3필드) -> StudyPredictionView(4필드)
    public StudyPredictionView toView(CoachPredictionView p) {
        var copy = copyService.render(p);

        ExposureLevel exposureLevel = ExposurePolicy.decide(p);

        // DETAIL일 때만 reason 노출
        String reason =
                exposureLevel == ExposureLevel.DETAIL
                        ? p.reasonCode().description()
                        : "";

        // level / expectedOutcome / reason / minimalAction 순서 고정
        return new StudyPredictionView(
                p.level().name(),
                copy.expectedOutcome(),
                reason,
                copy.minimalAction()
        );
    }

    // 과도기 유지(DTO)
    public StudyPredictionView toDto(CoachPredictionView p) {
        return toView(p);
    }
}