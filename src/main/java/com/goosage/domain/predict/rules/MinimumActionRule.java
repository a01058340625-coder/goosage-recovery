package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.recovery.RecoverySnapshot;

@Component
public class MinimumActionRule implements PredictionRule {

    @Override
    public int priority() {
        return 999; // 항상 마지막
    }

    @Override
    public boolean matches(RecoverySnapshot s) {
        return true; // 무조건 매칭 (최후 안전 Rule)
    }

    @Override
    public Prediction apply(RecoverySnapshot s) {
        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.MINIMUM_ACTION,
                "오늘 최소 1개 행동부터 시작하자.",
                Map.of(
                        "streakDays", s.streakDays(),
                        "daysSinceLastEvent", s.daysSinceLastEvent(),
                        "recentEventCount3d", s.recentEventCount3d(),
                        "eventsCount", s.state().eventsCount(),
                        "urgeLogs", s.state().urgeLogs(),
                        "betAttempts", s.state().betAttempts(),
                        "betBlockedCount", s.state().betBlockedCount(),
                        "recoveryActionCount", s.state().recoveryActionCount(),
                        "relapseSignalCount", s.state().relapseSignalCount()
                )
        );
    }
}