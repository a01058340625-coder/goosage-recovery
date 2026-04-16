package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.recovery.RecoverySnapshot;

@Component
public class DefenseProgressRule implements PredictionRule {

    @Override
    public int priority() {
        return 9;
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

        if (!s.studiedToday()) {
            return false;
        }

        if (urge > 0 || attempts > 0 || relapse > 0) {
            return false;
        }

        // 방어 진행은 blocked가 실제로 있을 때만 인정
        if (blocked <= 0) {
            return false;
        }

        // recovery 없이 blocked만 있는 불안정 케이스 제외
        if (recovery <= 0) {
            return false;
        }

        // 너무 얇은 recent는 제외
        if (s.recentEventCount3d() < 4) {
            return false;
        }

        // streak도 최소 확보
        if (s.streakDays() < 3) {
            return false;
        }

        return true;
    }

    @Override
    public Prediction apply(RecoverySnapshot s) {
        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.RECOVERY_PROGRESS,
                "방어 흐름이 유지되고 있어. 지금 회복 행동을 이어가자.",
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