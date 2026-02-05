package com.goosage.service.study.predict;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.goosage.service.study.predict.model.Prediction;
import com.goosage.service.study.predict.model.PredictionLevel;
import com.goosage.service.study.predict.rule.PredictionRule;
import com.goosage.service.study.predict.model.PredictionInput;

@Service
public class PredictionServiceImpl implements PredictionService {

    private final List<PredictionRule> rules;

    public PredictionServiceImpl(List<PredictionRule> rules) {
        this.rules = rules.stream()
                .sorted(Comparator.comparingInt(PredictionRule::priority))
                .toList();
    }

    @Override
    public Prediction predict(PredictionInput input) {
        return rules.stream()
                .filter(r -> r.matches(input))
                .findFirst()
                .map(r -> r.predict(input))
                .orElse(defaultPrediction());
    }

    private Prediction defaultPrediction() {
        // 룰이 하나도 매칭되지 않았을 때는 "과장하지 않는" 중립 WARNING
        return new Prediction(
                PredictionLevel.WARNING,
                "지금 상태는 유지 가능하지만, 공백이 길어지면 리스크가 커집니다",
                "명시적 위험 규칙에 해당하지 않음",
                "이벤트 1회"
        );
    }
}
