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
        return 8;
    }

    @Override
    public boolean matches(RecoverySnapshot s) {
        if (s == null || s.state() == null) {
            return false;
        }

     // recoveryAction 없어도 "차단 상태"는 회복 흐름으로 본다
        if (s.state().recoveryActionCount() <= 0 
            && s.state().betBlockedCount() <= 0) {
            return false;
        }

        // relapse는 RecoveryProgress가 아니라 RelapseRiskRule에서 먼저 처리
        if (s.state().relapseSignalCount() > 0) {
            return false;
        }

        // 진짜 회복 흐름만 잡음: 시도 또는 차단 이후 회복 행동
        return s.state().betAttempts() > 0
                || s.state().betBlockedCount() > 0;
    }

    @Override
    public Prediction apply(RecoverySnapshot s) {
        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.RECOVERY_PROGRESS,
                "위험 신호를 정리하며 회복 중이야. 흐름을 이어가자.",
                Map.of(
                        "streakDays", s.streakDays(),
                        "daysSinceLastEvent", s.daysSinceLastEvent(),
                        "recentEventCount3d", s.recentEventCount3d(),
                        "eventsCount", s.state() != null ? s.state().eventsCount() : 0,
                        "urgeLogs", s.state() != null ? s.state().urgeLogs() : 0,
                        "betAttempts", s.state() != null ? s.state().betAttempts() : 0,
                        "betBlockedCount", s.state() != null ? s.state().betBlockedCount() : 0,
                        "recoveryActionCount", s.state() != null ? s.state().recoveryActionCount() : 0,
                        "relapseSignalCount", s.state() != null ? s.state().relapseSignalCount() : 0
                )
        );
    }
}