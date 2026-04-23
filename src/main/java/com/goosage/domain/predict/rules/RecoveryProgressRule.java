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

        System.out.println(
                "[DEBUG] RecoveryProgressRule entered"
                        + " recovery=" + s.state().recoveryActionCount()
                        + " relapse=" + s.state().relapseSignalCount()
                        + " urge=" + s.state().urgeLogs()
                        + " attempts=" + s.state().betAttempts()
                        + " blocked=" + s.state().betBlockedCount()
                        + " events=" + s.state().eventsCount()
                        + " recent3d=" + s.recentEventCount3d()
                        + " streak=" + s.streakDays()
                        + " daysSinceLast=" + s.daysSinceLastEvent()
        );

        int urge = s.state().urgeLogs();
        int attempts = s.state().betAttempts();
        int blocked = s.state().betBlockedCount();
        int recovery = s.state().recoveryActionCount();
        int relapse = s.state().relapseSignalCount();
        int events = s.state().eventsCount();

        int recent3d = s.recentEventCount3d();
        int streak = s.streakDays();
        int daysSinceLast = s.daysSinceLastEvent();

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

        // relapse가 recovery보다 강하면 아직 progress 아님
        if (relapse > recovery) {
            return false;
        }

        if (blocked > recovery) {
            return false;
        }

        /*
         * Day62 strong-safe는 progress로 먹지 않게 차단
         * 621 보호
         */
        /*
         * Day62 strong-safe는 progress로 먹지 않게 차단
         */
        if (recent3d >= 5
                && daysSinceLast == 0
                && recovery >= 2
                && events >= 2
                && blocked == 0
                && relapse == 0
                && urge == 0
                && attempts == 0
                && streak >= 4) {
            return false;
        }

        /*
         * Day68 cumulative-stable-safe도 progress에서 제외
         */
        if (recent3d >= 2
                && daysSinceLast == 0
                && recovery >= 2
                && events >= 2
                && blocked == 0
                && relapse == 0
                && urge == 0
                && attempts == 0
                && streak >= 2) {
            return false;
        }

        /*
         * Day61 long-streak stable-safe도 progress에서 제외
         */
        if (recent3d == 3
                && streak >= 5
                && recovery == 1
                && daysSinceLast <= 1
                && blocked == 0
                && relapse == 0
                && urge == 0
                && attempts == 0) {
            return false;
        }

        // relapse 흔적이 남아 있어도 recovery가 따라잡은 상태는 progress 재진입 허용
        if (relapse > 0) {
            if (recovery >= relapse
                    && events >= 2
                    && recent3d >= 3
                    && daysSinceLast == 0) {
                return true;
            }
            return false;
        }

        // very-thin recovery 시작 상태
        if (recovery >= 1
                && events >= 1
                && daysSinceLast == 0
                && recent3d == 1
                && streak <= 1) {
            return true;
        }

        if (recent3d < 2) {
            return false;
        }

        return true;
    }

    @Override
    public Prediction apply(RecoverySnapshot s) {
        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.RECOVERY_PROGRESS,
                "위험 신호가 약해졌고 회복 행동이 이어지고 있다. 지금 흐름을 유지하자.",
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