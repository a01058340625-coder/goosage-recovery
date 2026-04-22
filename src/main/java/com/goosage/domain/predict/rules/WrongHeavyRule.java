package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.recovery.RecoverySnapshot;

@Component
public class WrongHeavyRule implements PredictionRule {

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

        System.out.println(
                "[DEBUG] WrongHeavyRule entered"
                        + " urge=" + urge
                        + " attempts=" + attempts
                        + " blocked=" + blocked
                        + " recovery=" + recovery
                        + " relapse=" + relapse
                        + " events=" + s.state().eventsCount()
                        + " recent3d=" + s.recentEventCount3d()
                        + " studiedToday=" + s.studiedToday()
        );

        // blocked만 있는 방어 상태는 제외
        if (blocked > 0 && relapse == 0 && attempts == 0) {
            return false;
        }

        /*
         * Day66 핵심:
         * relapse 이후 recovery가 따라온 상태는 WRONG_HEAVY로 잡지 않는다.
         */
        if (urge == 0
                && attempts == 0
                && relapse > 0
                && recovery >= relapse) {
            System.out.println(
                    "[DEBUG] WrongHeavyRule bypass"
                            + " recovery=" + recovery
                            + " relapse=" + relapse
            );
            return false;
        }

        // 재발 신호 + 시도 있으면 바로 위험 누적
        if (relapse >= 1 && attempts >= 1) {
            return true;
        }

        // 재발 신호가 2건 이상이면 명확한 위험 누적
        if (relapse >= 2 && recovery < relapse) {
            return true;
        }

        // urge와 relapse가 같이 있으면 표면 회복으로 보지 않음
        if (relapse >= 1 && urge >= 1) {
            return true;
        }

        // 회복보다 위험 신호가 앞서면 위험 누적
        if (relapse > recovery) {
            return true;
        }

        // attempt가 누적되고 recovery가 못 따라오면 위험 누적
        if (attempts >= 2 && recovery <= attempts - 1) {
            return true;
        }

        return false;
    }

    @Override
    public Prediction apply(RecoverySnapshot s) {
        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.WRONG_HEAVY,
                "위험 신호가 누적되고 있어. 새 시도보다 회복 행동을 먼저 하자.",
                Map.of(
                        "urgeLogs", s.state().urgeLogs(),
                        "betAttempts", s.state().betAttempts(),
                        "betBlockedCount", s.state().betBlockedCount(),
                        "recoveryActionCount", s.state().recoveryActionCount(),
                        "relapseSignalCount", s.state().relapseSignalCount(),
                        "eventsCount", s.state().eventsCount(),
                        "attemptRatio", s.attemptRatio(),
                        "relapseRatio", s.relapseRatio()
                )
        );
    }
}