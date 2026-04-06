package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.recovery.RecoverySnapshot;

@Component
public class LowActivity3dRule implements PredictionRule {

    @Override
    public int priority() {
        return 30;
    }

    @Override
    public boolean matches(RecoverySnapshot s) {
        if (s.studiedToday()) return false;

        if (s.daysSinceLastEvent() >= 3) return false;

        return s.recentEventCount3d() <= 1
                && !(s.recentEventCount3d() == 0 && s.streakDays() == 0);
    }

    @Override
    public Prediction apply(RecoverySnapshot s) {
        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.LOW_ACTIVITY_3D,
                "최근 3일 행동 기록이 부족해. 오늘 최소 1개 행동부터 다시 시작하자.",
                Map.of(
                        "studiedToday", s.studiedToday(),
                        "recentEventCount3d", s.recentEventCount3d(),
                        "daysSinceLastEvent", s.daysSinceLastEvent(),
                        "streakDays", s.streakDays()
                )
        );
    }
}