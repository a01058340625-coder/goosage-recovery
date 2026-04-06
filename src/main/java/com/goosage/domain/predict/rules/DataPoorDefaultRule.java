package com.goosage.domain.predict.rules;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.recovery.RecoverySnapshot;

@Component
public class DataPoorDefaultRule implements PredictionRule {

    @Override
    public int priority() { return 20; }

    @Override
    public boolean matches(RecoverySnapshot s) {
        if (s.studiedToday()) return false;

        if (s.daysSinceLastEvent() >= 3) return false;

        return s.recentEventCount3d() == 0 && s.streakDays() == 0;
    }

    @Override
    public Prediction apply(RecoverySnapshot s) {

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
            "행동 기록이 부족해. 일단 최소 1개 행동부터 시작하자.",
            Map.copyOf(ev)
        );
    }
}