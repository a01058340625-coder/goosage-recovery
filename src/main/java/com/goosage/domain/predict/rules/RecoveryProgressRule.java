package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.recovery.RecoverySnapshot;

@Component
public class RecoveryProgressRule implements PredictionRule {

    @Override
    public int priority() {
        return 10;
    }

    @Override
    public boolean matches(RecoverySnapshot s) {
        if (s == null || s.state() == null) {
            return false;
        }

        int urge = s.state().urgeLogs();
        int attempts = s.state().betAttempts();
        int blocked = s.state().betBlockedCount();
        int recovery = s.state().recoveryActionCount();
        int relapse = s.state().relapseSignalCount();
        int events = s.state().eventsCount();

        if (recovery <= 0) {
            return false;
        }

        if (events <= 0) {
            return false;
        }

        if (urge > 0) {
            return false;
        }

        if (attempts > 0) {
            return false;
        }

        if (relapse > 0) {
            return false;
        }

        if (blocked > recovery) {
            return false;
        }

        // very-thin recovery 시작 상태: progress-check로 허용
        if (recovery >= 1
                && events >= 1
                && s.daysSinceLastEvent() == 0
                && s.recentEventCount3d() == 1
                && s.streakDays() <= 1) {
            return true;
        }

        if (s.recentEventCount3d() < 2) {
            return false;
        }

        return true;
    }

    @Override
    public Prediction apply(RecoverySnapshot s) {
        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.RECOVERY_PROGRESS,
                "위험 신호 없이 회복 행동을 이어가고 있어. 지금 흐름을 유지하자.",
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