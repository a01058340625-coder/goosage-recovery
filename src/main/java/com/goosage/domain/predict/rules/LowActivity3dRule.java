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
        if (s == null || s.state() == null) {
            return false;
        }

        int events = s.state().eventsCount();
        int recovery = s.state().recoveryActionCount();
        int attempts = s.state().betAttempts();
        int relapse = s.state().relapseSignalCount();
        int urge = s.state().urgeLogs();
        int blocked = s.state().betBlockedCount();

        if (attempts > 0 || relapse > 0) {
            return false;
        }

        // 회복 행동이 실제로 시작된 경우는 low-activity fallback으로 바로 보내지 않는다.
        if (recovery >= 1
                && events >= 1
                && s.daysSinceLastEvent() == 0
                && urge == 0
                && attempts == 0
                && relapse == 0
                && blocked <= recovery) {
            return false;
        }

        return s.recentEventCount3d() <= 1
                && s.streakDays() <= 1
                && events <= 1
                && recovery <= 1;
    }

    @Override
    public Prediction apply(RecoverySnapshot s) {
        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.LOW_ACTIVITY_3D,
                "회복 흐름이 아직 약해. 오늘 1회 더 행동해서 리듬을 붙이자.",
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