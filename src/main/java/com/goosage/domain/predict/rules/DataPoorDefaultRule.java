package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.study.StudySnapshot;

@Component

public class DataPoorDefaultRule implements PredictionRule {

    @Override
    public int priority() {
        return 5;
    }

    @Override
    public boolean matches(StudySnapshot s) {
        // fresh/empty user 판단: 최근 3일 이벤트 0 + streak 0
        return s.recentEventCount3d() == 0 && s.streakDays() == 0;
    }

    @Override
    public Prediction apply(StudySnapshot s) {

        Map<String, Object> ev = new java.util.LinkedHashMap<>();
        ev.put("recentEventCount3d", s.recentEventCount3d());
        ev.put("streakDays", s.streakDays());

        // primitive라 null 불가능 → 그대로 넣는다
        ev.put("daysSinceLastEvent", s.daysSinceLastEvent());

        // 이건 nullable
        if (s.lastEventAt() != null) {
            ev.put("lastEventAt", s.lastEventAt().toString());
        }

        return Prediction.of(
            PredictionLevel.WARNING,
            PredictionReasonCode.DATA_POOR,
            "학습 데이터가 부족해. 일단 최소 1개 이벤트부터 쌓아보자.",
            java.util.Map.copyOf(ev)
        );
    }
}