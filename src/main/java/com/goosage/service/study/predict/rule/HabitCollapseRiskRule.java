package com.goosage.service.study.predict.rule;

import org.springframework.stereotype.Component;

import com.goosage.service.study.predict.model.Prediction;
import com.goosage.service.study.predict.model.PredictionInput;
import com.goosage.service.study.predict.model.PredictionLevel;

@Component
public class HabitCollapseRiskRule implements PredictionRule {

    @Override
    public boolean matches(PredictionInput i) {
        return i.daysSinceLastEvent() >= 3;
    }

    @Override
    public Prediction predict(PredictionInput i) {
        return new Prediction(
                PredictionLevel.RISK,
                "학습 흐름이 무너질 가능성이 큽니다",
                i.daysSinceLastEvent() + "일 연속 공백",
                "퀴즈 1문제"
        );
    }

    @Override
    public int priority() {
        return 10;
    }
}
