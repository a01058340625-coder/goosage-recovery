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

        int urge = s.state().urgeLogs();
        int attempts = s.state().betAttempts();
        int blocked = s.state().betBlockedCount();
        int recovery = s.state().recoveryActionCount();
        int relapse = s.state().relapseSignalCount();

        // recovery action이 실제로 있어야 회복 흐름으로 본다
        if (recovery <= 0) {
            return false;
        }

        // 재발 신호가 있으면 progress가 아니라 relapse 쪽에서 처리
        if (relapse > 0) {
            return false;
        }

        // 실제 시도(attempt)가 있으면 아직 progress보다 risk가 우선
        if (attempts > 0) {
            return false;
        }

        // urge가 recovery보다 너무 우세하면 아직 progress 아님
        if (urge >= recovery + 2) {
            return false;
        }

        // blocked가 recovery보다 너무 우세하면 아직 progress 아님
        if (blocked >= recovery + 2) {
            return false;
        }

        // recovery가 실제로 중심축이거나 최소 균형권일 때만 progress 허용
        return true;
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