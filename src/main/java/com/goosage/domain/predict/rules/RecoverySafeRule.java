package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.recovery.RecoverySnapshot;

@Component
public class RecoverySafeRule implements PredictionRule {

    @Override
    public int priority() {
        return 6;
    }

    @Override
    public boolean matches(RecoverySnapshot s) {
        if (s == null || s.state() == null) {
            return false;
        }
        if (!s.studiedToday()) {
            return false;
        }

        int urge = s.state().urgeLogs();
        int attempts = s.state().betAttempts();
        int blocked = s.state().betBlockedCount();
        int recovery = s.state().recoveryActionCount();
        int relapse = s.state().relapseSignalCount();

        return recovery >= 1
                && attempts == 0
                && blocked == 0
                && relapse == 0
                && urge == 0
                && s.recentEventCount3d() >= 3
                && s.streakDays() >= 3
                && s.daysSinceLastEvent() <= 1;
    }

    @Override
    public Prediction apply(RecoverySnapshot s) {
        return Prediction.of(
                PredictionLevel.SAFE,
                PredictionReasonCode.RECOVERY_SAFE,
                "회복 흐름이 안정권에 들어왔다. 오늘은 점검만 하고 유지하자.",
                Map.of(
                        "streakDays", s.streakDays(),
                        "daysSinceLastEvent", s.daysSinceLastEvent(),
                        "recentEventCount3d", s.recentEventCount3d(),
                        "eventsCount", s.state().eventsCount(),
                        "urgeLogs", s.state().urgeLogs(),
                        "betAttempts", s.state().betAttempts(),
                        "betBlockedCount", s.state().betBlockedCount(),
                        "recoveryActionCount", s.state().recoveryActionCount(),
                        "relapseSignalCount", s.state().relapseSignalCount(),
                        "studiedToday", s.studiedToday()
                )
        );
    }
}