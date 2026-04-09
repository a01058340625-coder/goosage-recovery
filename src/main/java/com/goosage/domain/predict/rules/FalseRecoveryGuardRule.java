package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.recovery.RecoverySnapshot;

@Component
public class FalseRecoveryGuardRule implements PredictionRule {

    private static final int EVENTS_MIN = 5;
    private static final double URGE_RATIO_MAX = 0.55;
    private static final double ATTEMPT_RATIO_MIN = 0.30;

    @Override
    public int priority() {
        // RecoverySafeRule보다 먼저 막아야 함
        return 14;
    }

    @Override
    public boolean matches(RecoverySnapshot s) {
        if (s == null || s.state() == null) {
            return false;
        }

        int events = s.state().eventsCount();
        int urgeLogs = s.state().urgeLogs();
        int betAttempts = s.state().betAttempts();
        int recoveryActionCount = s.state().recoveryActionCount();
        int relapseSignalCount = s.state().relapseSignalCount();

        if (events < EVENTS_MIN) {
            return false;
        }

        if (recoveryActionCount <= 0) {
            return false;
        }

        double urgeRatio = events <= 0 ? 0.0 : (double) urgeLogs / events;
        double attemptRatio = events <= 0 ? 0.0 : (double) betAttempts / events;

        boolean noRelapseContext = relapseSignalCount == 0;
        boolean lowAttemptQuality = attemptRatio < ATTEMPT_RATIO_MIN;
        boolean urgeHeavy = urgeRatio > URGE_RATIO_MAX;

        // recoveryAction은 있는데 실제 회복 맥락이 빈약하고
        // 동시에 품질도 낮으면 가짜 회복으로 본다.
        return noRelapseContext && (lowAttemptQuality || urgeHeavy);
    }

    @Override
    public Prediction apply(RecoverySnapshot s) {
        int events = s.state().eventsCount();
        int urgeLogs = s.state().urgeLogs();
        int betAttempts = s.state().betAttempts();
        int recoveryActionCount = s.state().recoveryActionCount();
        int relapseSignalCount = s.state().relapseSignalCount();

        double urgeRatio = events <= 0 ? 0.0 : (double) urgeLogs / events;
        double attemptRatio = events <= 0 ? 0.0 : (double) betAttempts / events;

        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.RECOVERY_PROGRESS,
                "회복 행동이 일부 쌓였지만 아직 실제 회복 안정으로 보기엔 맥락이 약하다. 한 번 더 회복 행동 흐름을 확인하자.",
                Map.of(
                        "eventsCount", events,
                        "urgeLogs", urgeLogs,
                        "betAttempts", betAttempts,
                        "recoveryActionCount", recoveryActionCount,
                        "relapseSignalCount", relapseSignalCount,
                        "urgeRatio", urgeRatio,
                        "attemptRatio", attemptRatio,
                        "urgeRatioMax", URGE_RATIO_MAX,
                        "attemptRatioMin", ATTEMPT_RATIO_MIN
                )
        );
    }
}