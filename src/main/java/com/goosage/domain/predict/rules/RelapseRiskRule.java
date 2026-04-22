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
        return 7;
    }

    @Override
    public boolean matches(RecoverySnapshot s) {
        if (s == null || s.state() == null) {
            return false;
        }

        int urge = s.state().urgeLogs();
        int attempts = s.state().betAttempts();
        int relapse = s.state().relapseSignalCount();
        int recovery = s.state().recoveryActionCount();
        int recent3d = s.recentEventCount3d();
        int daysSinceLast = s.daysSinceLastEvent();

        // 현재 urge/attempt가 있으면 즉시 risk
        if (urge > 0 || attempts > 0) {
            return true;
        }

        // relapse 흔적만 남은 경우는 무조건 risk로 잡지 않는다.
        if (relapse <= 0) {
            return false;
        }

        // relapse가 recovery보다 강하면 risk 유지
        if (relapse > recovery) {
            return true;
        }

        // 아주 얇은 회복 상태에서는 아직 risk 유지
        if (relapse >= 1
                && recovery <= 0
                && recent3d <= 1
                && daysSinceLast == 0) {
            return true;
        }

        return false;
    }

    @Override
    public Prediction apply(RecoverySnapshot s) {
        int urge = s.state().urgeLogs();
        int attempts = s.state().betAttempts();
        int relapse = s.state().relapseSignalCount();

        PredictionLevel level = (attempts >= 1 || (relapse >= 1 && urge >= 1))
                ? PredictionLevel.DANGER
                : PredictionLevel.WARNING;

        return Prediction.of(
                level,
                PredictionReasonCode.RELAPSE_RISK,
                "재시도 또는 재발 위험 신호가 현재 흐름에 남아 있다. 지금 바로 회복 행동으로 흐름을 끊자.",
                Map.of(
                        "streakDays", s.streakDays(),
                        "daysSinceLastEvent", s.daysSinceLastEvent(),
                        "recentEventCount3d", s.recentEventCount3d(),
                        "eventsCount", s.state().eventsCount(),
                        "urgeLogs", urge,
                        "betAttempts", attempts,
                        "betBlockedCount", s.state().betBlockedCount(),
                        "recoveryActionCount", s.state().recoveryActionCount(),
                        "relapseSignalCount", relapse
                )
        );
    }
}