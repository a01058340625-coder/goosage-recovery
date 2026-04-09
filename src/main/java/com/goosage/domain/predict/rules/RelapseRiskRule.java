package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.recovery.RecoverySnapshot;

@Component
public class RelapseRiskRule implements PredictionRule {

    @Override
    public int priority() {
        return 12; // HabitStableRule(25)보다 먼저
    }

    @Override
    public boolean matches(RecoverySnapshot s) {
        if (s == null || s.state() == null) {
            return false;
        }

        int attempts = s.state().betAttempts();
        int relapse = s.state().relapseSignalCount();

        return attempts > 0 || relapse > 0;
    }

    @Override
    public Prediction apply(RecoverySnapshot s) {
        int attempts = s.state().betAttempts();
        int relapse = s.state().relapseSignalCount();

        PredictionLevel level = (relapse >= 2 || attempts >= 2)
                ? PredictionLevel.DANGER
                : PredictionLevel.WARNING;

        return Prediction.of(
                level,
                PredictionReasonCode.RELAPSE_RISK,
                "재시도 또는 재발 신호가 감지됐다. 지금 바로 회복 행동으로 흐름을 끊자.",
                Map.of(
                        "streakDays", s.streakDays(),
                        "daysSinceLastEvent", s.daysSinceLastEvent(),
                        "recentEventCount3d", s.recentEventCount3d(),
                        "eventsCount", s.state().eventsCount(),
                        "urgeLogs", s.state().urgeLogs(),
                        "betAttempts", attempts,
                        "betBlockedCount", s.state().betBlockedCount(),
                        "recoveryActionCount", s.state().recoveryActionCount(),
                        "relapseSignalCount", relapse
                )
        );
    }
}