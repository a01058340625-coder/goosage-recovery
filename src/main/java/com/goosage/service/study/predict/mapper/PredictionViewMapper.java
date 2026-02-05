package com.goosage.service.study.predict.mapper;

import org.springframework.stereotype.Component;

import com.goosage.api.view.study.PredictionDto;
import com.goosage.service.study.predict.model.Prediction;

@Component
public class PredictionViewMapper {

    public PredictionDto toDto(Prediction p) {
        if (p == null) return null;
        return new PredictionDto(
                p.level().name(),
                p.expectedOutcome(),
                p.reason(),
                p.minimalAction()
        );
    }
}
