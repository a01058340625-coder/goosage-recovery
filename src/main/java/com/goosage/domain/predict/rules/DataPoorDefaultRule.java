// src/main/java/com/goosage/domain/predict/rules/DataPoorDefaultRule.java
package com.goosage.domain.predict.rules;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.study.StudySnapshot;

@Component
public class DataPoorDefaultRule implements PredictionRule {

    // ✅ TODAY_DONE 다음
    @Override
    public int priority() { return 20; }

    @Override
    public boolean matches(StudySnapshot s) {
        if (s.studiedToday()) return false;

        // 장기 공백은 collapse 쪽으로 넘긴다
        if (s.daysSinceLastEvent() >= 3) return false;

        return s.recentEventCount3d() == 0 && s.streakDays() == 0;
    }

    @Override
    public Prediction apply(StudySnapshot s) {

        Map<String, Object> ev = new LinkedHashMap<>();
        ev.put("recentEventCount3d", s.recentEventCount3d());
        ev.put("streakDays", s.streakDays());
        ev.put("daysSinceLastEvent", s.daysSinceLastEvent());

        if (s.lastEventAt() != null) {
            ev.put("lastEventAt", s.lastEventAt().toString());
        }

        return Prediction.of(
            PredictionLevel.WARNING,
            PredictionReasonCode.DATA_POOR,
            "학습 데이터가 부족해. 일단 최소 1개 이벤트부터 쌓아보자.",
            Map.copyOf(ev)
        );
    }
}