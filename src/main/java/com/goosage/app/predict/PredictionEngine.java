// src/main/java/com/goosage/app/predict/PredictionEngine.java
package com.goosage.app.predict;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.study.StudySnapshot;

@Component
public class PredictionEngine {

    private final List<PredictionRule> rules;

    public PredictionEngine(List<PredictionRule> rules) {
        // ✅ priority 숫자 작을수록 먼저 적용(우선순위 높음)
        this.rules = rules.stream()
                .sorted(Comparator.comparingInt(PredictionRule::priority))
                .toList();
    }

    public Prediction predict(StudySnapshot s) {
        for (var r : rules) {
            if (r.matches(s)) return r.apply(s);
        }

        // ✅ 정공법: 여기까지 오면 안 됨 (DefaultFallbackRule이 항상 true)
        // 그래도 혹시 모를 안전망(절대경로)
        throw new IllegalStateException("No PredictionRule matched. DefaultFallbackRule missing?");
    }
}