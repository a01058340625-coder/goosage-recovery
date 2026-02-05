package com.goosage.service.study.predict.rule;

import org.springframework.stereotype.Component;

import com.goosage.service.study.predict.model.Prediction;
import com.goosage.service.study.predict.model.PredictionInput;
import com.goosage.service.study.predict.model.PredictionLevel;

@Component
public class StableStateRule implements PredictionRule {

    @Override
    public boolean matches(PredictionInput i) {
        return i.streakDays() >= 1 && i.daysSinceLastEvent() == 0;
    }

    @Override
    public Prediction predict(PredictionInput i) {
        return new Prediction(
                PredictionLevel.SAFE,
                "현재 학습 흐름을 유지하고 있습니다",
                "오늘 학습 완료",
                "유지"
        );
    }

    @Override
    public int priority() {
        return 30;
    }
}
