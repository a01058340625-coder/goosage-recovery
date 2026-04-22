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

    @Override
    public int priority() {
        // RecoveryProgress / DefenseProgress보다 먼저 차단
        return 8;
    }

    @Override
    public boolean matches(RecoverySnapshot s) {
        if (s == null || s.state() == null) {
            return false;
        }

        int urge = s.state().urgeLogs();
        int attempts = s.state().betAttempts();
        int recovery = s.state().recoveryActionCount();
        int relapse = s.state().relapseSignalCount();

        System.out.println(
                "[DEBUG] FalseRecoveryGuardRule entered"
                        + " urge=" + urge
                        + " attempts=" + attempts
                        + " recovery=" + recovery
                        + " relapse=" + relapse
                        + " events=" + s.state().eventsCount()
                        + " recent3d=" + s.recentEventCount3d()
                        + " studiedToday=" + s.studiedToday()
        );

        if (recovery <= 0) {
            return false;
        }

        // urge only는 RelapseRiskRule로 보내기 위해 제외
        if (relapse == 0 && attempts == 0 && urge > 0) {
            return false;
        }

        /*
         * Day66 핵심:
         * recovery가 relapse를 따라잡았고
         * 현재 urge/attempt가 없으면 false recovery로 보지 않는다.
         * 아래 RecoveryProgress / ReviewWrongNeeded로 내려보낸다.
         */
        if (urge == 0
                && attempts == 0
                && relapse > 0
                && recovery >= relapse) {
            System.out.println(
                    "[DEBUG] FalseRecoveryGuardRule bypass"
                            + " recovery=" + recovery
                            + " relapse=" + relapse
            );
            return false;
        }

        if (attempts > 0) {
            return true;
        }

        if (relapse > 0) {
            return true;
        }

        return false;
    }

    @Override
    public Prediction apply(RecoverySnapshot s) {
        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.WRONG_HEAVY,
                "회복 행동이 있어도 위험 신호가 남아 있어. 먼저 위험 신호를 정리하자.",
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