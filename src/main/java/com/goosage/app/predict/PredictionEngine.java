package com.goosage.app.predict;

import java.util.Comparator;
import java.util.List;

import com.goosage.domain.predict.*;
import com.goosage.domain.study.StudySnapshot;

public class PredictionEngine {

    private final List<PredictionRule> rules;

    public PredictionEngine(List<PredictionRule> rules) {
        this.rules = rules.stream()
            .sorted(Comparator.comparingInt(PredictionRule::priority))
            .toList();
    }

    public Prediction predict(StudySnapshot s) {
        for (var r : rules) {
            if (r.matches(s)) return r.apply(s);
        }
        // v1.5에서는 "Fallback" 금지 → 명시 디폴트로만 둔다 (이름도 Fallback 금지)
        return new Prediction(
            PredictionLevel.SAFE,
            PredictionReasonCode.LOW_ACTIVITY_3D, // 임시 금지. 아래처럼 EXPLICIT_DEFAULT로 분리 추천.
            java.util.Map.of("note", "no rule matched")
        );
    }
}
