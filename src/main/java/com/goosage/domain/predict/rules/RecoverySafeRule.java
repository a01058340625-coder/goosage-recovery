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
        /*
         * Day62 strong-safe:
         * recent / streak / recovery 누적이 충분한 강한 safe
         */
        if (recent3d >= 5
                && daysSinceLast == 0
                && recovery >= 2
                && events >= 2
                && streak >= 4
                && blocked == 0
                && relapse == 0
                && urge == 0
                && attempts == 0) {
            return true;
        }

        /*
         * Day68 cumulative-stable-safe:
         * 회복 행동이 누적되고 최근 연결이 유지되며
         * 위험 신호가 없으면 SAFE로 승격
         */
        if (recent3d >= 2
                && daysSinceLast == 0
                && recovery >= 2
                && events >= 2
                && streak >= 2
                && blocked == 0
                && relapse == 0
                && urge == 0
                && attempts == 0) {
            return true;
        }

        /*
         * Day61 long-streak stable-safe:
         * 얇지만 길게 유지된 회복 연결은 SAFE 허용
         */
        if (recent3d == 3
                && daysSinceLast <= 1
                && streak >= 5
                && recovery == 1
                && events >= 1
                && blocked == 0
                && relapse == 0
                && urge == 0
                && attempts == 0) {
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