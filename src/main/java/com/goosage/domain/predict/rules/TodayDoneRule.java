package com.goosage.domain.predict.rules;

import static java.util.Map.entry;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.recovery.RecoverySnapshot;

@Component
public class TodayDoneRule implements PredictionRule {

    private static final int RECOVERY_MIN = 3;
    private static final double RECOVERY_RATIO_MIN = 0.60;
    private static final double URGE_RATIO_MAX = 0.40;

    @Override
    public int priority() {
        return 5;
    }

    @Override
    public boolean matches(RecoverySnapshot s) {
        if (s == null || s.state() == null) {
            return false;
        }

        if (!s.studiedToday()) {
            return false;
        }

        // recovery-safe 케이스는 RecoverySafeRule에게 넘김
        if (s.isRecoverySafe()) {
            return false;
        }

        int recovery = s.state().recoveryActionCount();
        int relapse = s.state().relapseSignalCount();

        double recoveryRatio = s.recoveryRatio();
        double urgeRatio = s.urgeRatio();

        return recovery >= RECOVERY_MIN
                && relapse == 0
                && recoveryRatio >= RECOVERY_RATIO_MIN
                && urgeRatio <= URGE_RATIO_MAX;
    }

    @Override
    public Prediction apply(RecoverySnapshot s) {
        return Prediction.of(
                PredictionLevel.SAFE,
                PredictionReasonCode.TODAY_DONE,
                "오늘 회복 행동이 충분히 이루어졌다. 현재 흐름을 유지하자.",
                Map.ofEntries(
                        entry("streakDays", s.streakDays()),
                        entry("daysSinceLastEvent", s.daysSinceLastEvent()),
                        entry("recentEventCount3d", s.recentEventCount3d()),
                        entry("eventsCount", s.state().eventsCount()),
                        entry("urgeLogs", s.state().urgeLogs()),
                        entry("betAttempts", s.state().betAttempts()),
                        entry("betBlockedCount", s.state().betBlockedCount()),
                        entry("recoveryActionCount", s.state().recoveryActionCount()),
                        entry("relapseSignalCount", s.state().relapseSignalCount()),
                        entry("studiedToday", s.studiedToday()),
                        entry("recoveryMin", RECOVERY_MIN),
                        entry("recoveryRatio", s.recoveryRatio()),
                        entry("urgeRatio", s.urgeRatio()),
                        entry("recoveryRatioMin", RECOVERY_RATIO_MIN),
                        entry("urgeRatioMax", URGE_RATIO_MAX)
                )
        );
    }
}