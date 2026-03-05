package com.goosage.api.view.study;

import org.springframework.stereotype.Component;

import com.goosage.app.predict.PredictionCopyService;

@Component
public class PredictionViewMapper {

    private final PredictionCopyService copyService;

    public PredictionViewMapper(PredictionCopyService copyService) {
        this.copyService = copyService;
    }

    // v1.4 계약(View) - api.view.study.StudyPredictionView (4필드 record)에 맞춘다
    public StudyPredictionView toView(CoachPredictionView p) {
        var copy = copyService.render(p);

        ExposureLevel exposureLevel = ExposurePolicy.decide(p);

        // DETAIL일 때만 reason을 채움 (기존 의도 유지)
        String reason =
                exposureLevel == ExposureLevel.DETAIL
                        ? p.reasonCode().description()
                        : "";

        // level / expectedOutcome / reason / minimalAction 순서 고정
        return new StudyPredictionView(
                p.level().name(),
                copy.expectedOutcome(),
                reason,
                "" // minimalAction은 이후 확장
        );
    }

    // 과도기 유지(DTO) - 기존 호출부가 있으면 toView로 위임
    public StudyPredictionView toDto(CoachPredictionView p) {
        return toView(p);
    }
}
