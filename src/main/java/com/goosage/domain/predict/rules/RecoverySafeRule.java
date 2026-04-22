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
        int events = s.state().eventsCount();

        int recent3d = s.recentEventCount3d();
        int streak = s.streakDays();
        int daysSinceLast = s.daysSinceLastEvent();

        if (urge > 0 || attempts > 0 || blocked > 0 || relapse > 0) {
            return false;
        }

        if (recovery < 1) {
            return false;
        }

        if (daysSinceLast > 1) {
            return false;
        }

        /*
         * Day62 strong-safe:
         * 621 같은 케이스 보호
         * recent 높고, gap 없고, recovery가 충분하면 SAFE
         */
        if (recent3d >= 5
                && daysSinceLast == 0
                && recovery >= 2
                && events >= 2
                && streak >= 4) {
            return true;
        }

        /*
         * Day61 long-streak stable-safe:
         * 611 같은 케이스만 좁게 SAFE 허용
         * recent=3 / recovery=1 / streak>=5 / low-risk / connected
         */
        if (recent3d == 3
                && streak >= 5
                && recovery == 1
                && events >= 1) {
            return true;
        }

        return false;
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