package com.goosage.service.study.predict.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.goosage.api.view.study.PredictionDto;
import com.goosage.domain.study.ExposurePolicy;
import com.goosage.service.study.predict.copy.PredictionCopyService;
import com.goosage.service.study.predict.model.*;

@Component
public class PredictionViewMapper {

    private final PredictionCopyService copyService;

    public PredictionViewMapper(PredictionCopyService copyService) {
        this.copyService = copyService;
    }

    // v1.4 계약(View)
    public PredictionView toView(Prediction p) {
        var copy = copyService.render(p);

        ExposureLevel exposureLevel = ExposurePolicy.decide(p);

        List<EvidenceView> evidences =
                exposureLevel == ExposureLevel.DETAIL
                        ? List.of(
                            new EvidenceView("streakDays", String.valueOf(p.evidence().streakDays())),
                            new EvidenceView("daysSinceLastEvent", String.valueOf(p.evidence().daysSinceLastEvent()))
                        )
                        : List.of();

        List<String> reasons =
                exposureLevel == ExposureLevel.DETAIL
                        ? List.of(p.reasonCode().description())
                        : List.of();

        return new PredictionView(
                copy.expectedOutcome(),
                exposureLevel,
                evidences,
                reasons
        );
    }

    // 과도기 유지(DTO)
    public PredictionDto toDto(Prediction p) {
        PredictionView view = toView(p);
        String reason = view.reasons().isEmpty() ? "" : String.join(" / ", view.reasons());

        return new PredictionDto(
                p.level().name(),
                view.copy(),
                reason,
                "" // minimalAction은 이후 확장
        );
    }
}
