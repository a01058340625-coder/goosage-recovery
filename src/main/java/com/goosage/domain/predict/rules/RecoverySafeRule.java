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

    private static final int EVENTS_MIN = 4;
    private static final int RECOVERY_MIN = 2;
    private static final int RECENT_3D_MIN = 2;

    @Override
    public int priority() {
        return 6;
    }

    @Override
    public boolean matches(RecoverySnapshot s) {
        if (s == null || s.state() == null) return false;
        if (!s.studiedToday()) return false;

        int recovery = s.state().recoveryActionCount();
        int attempts = s.state().betAttempts();
        int relapse = s.state().relapseSignalCount();

        // 🔥 recovery dominance 명확히
        return recovery >= 3
            && recovery > (attempts + relapse)
            && s.recentEventCount3d() >= 2;
    }

    @Override
    public Prediction apply(RecoverySnapshot s) {
        return Prediction.of(
                PredictionLevel.SAFE,
                PredictionReasonCode.RECOVERY_SAFE,
                "회복 행동이 위험 신호보다 우세하여 회복 안정권에 들어왔다.",
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