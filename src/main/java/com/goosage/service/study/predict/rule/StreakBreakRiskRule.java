package com.goosage.service.study.predict.rule;

import org.springframework.stereotype.Component;

import com.goosage.service.study.predict.model.Prediction;
import com.goosage.service.study.predict.model.PredictionInput;
import com.goosage.service.study.predict.model.PredictionLevel;

@Component
public class StreakBreakRiskRule implements PredictionRule {

    @Override
    public boolean matches(PredictionInput i) {
        return i.streakDays() >= 3 && i.daysSinceLastEvent() >= 1;
    }

    @Override
    public Prediction predict(PredictionInput i) {
        return new Prediction(
                PredictionLevel.WARNING,
                "연속 기록이 끊길 가능성이 있습니다",
                "연속 " + i.streakDays() + "일 이후 오늘 학습 공백",
                "오늘 이벤트 1회"
        );
    }

    @Override
    public int priority() {
        return 20;
    }
}
