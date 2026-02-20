package com.goosage.app.predict;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.study.StudySnapshot;

@Component
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

        // v1.5: 임시 디폴트 (나중에 EXPLICIT_DEFAULT 등으로 분리)
        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.DEFAULT_FALLBACK,
                Map.of(
                        "note", "no rule matched",
                        "streakDays", s.streakDays(),
                        "daysSinceLastEvent", s.daysSinceLastEvent(),
                        "recentEventCount3d", s.recentEventCount3d()
                )
        );
    }
}