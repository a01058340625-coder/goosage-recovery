package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.recovery.RecoverySnapshot;

@Component
public class ReviewWrongNeededRule implements PredictionRule {

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
                "[DEBUG] ReviewWrongNeededRule entered"
                        + " recovery=" + s.state().recoveryActionCount()
                        + " relapse=" + s.state().relapseSignalCount()
                        + " urge=" + s.state().urgeLogs()
                        + " attempts=" + s.state().betAttempts()
                        + " blocked=" + s.state().betBlockedCount()
                        + " events=" + s.state().eventsCount()
                        + " recent3d=" + s.recentEventCount3d()
                        + " studiedToday=" + s.studiedToday()
        );

        if (!s.studiedToday()) {
            return false;
        }

        int blocked = s.state().betBlockedCount();
        int recovery = s.state().recoveryActionCount();
        int attempts = s.state().betAttempts();
        int relapse = s.state().relapseSignalCount();
        int urge = s.state().urgeLogs();

        /*
         * attempt는 더 위험한 룰에서 처리
         */
        if (attempts > 0) {
            return false;
        }

        /*
         * 기존 blocked 우세 pending
         */
        if (blocked > 0 && blocked > recovery) {
            return true;
        }

        /*
         * Day66:
         * relapse 흔적은 남아 있지만
         * 현재 urge/attempt는 없고 recovery가 존재하는 상태는
         * risk-first 점검 상태로 본다.
         * (예: recent relapse hold)
         */
        if (relapse > 0
                && recovery > 0
                && urge == 0
                && attempts == 0) {
            return true;
        }

        return false;
    }

    @Override
    public Prediction apply(RecoverySnapshot s) {
        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.REVIEW_WRONG_PENDING,
                "위험 차단 신호가 남아 있어. 하나씩 회복 행동으로 정리하자.",
                Map.of(
                        "studiedToday", s.studiedToday(),
                        "streakDays", s.streakDays(),
                        "daysSinceLastEvent", s.daysSinceLastEvent(),
                        "recentEventCount3d", s.recentEventCount3d(),
                        "eventsCount", s.state().eventsCount(),
                        "urgeLogs", s.state().urgeLogs(),
                        "betAttempts", s.state().betAttempts(),
                        "betBlockedCount", s.state().betBlockedCount(),
                        "relapseSignalCount", s.state().relapseSignalCount(),
                        "recoveryActionCount", s.state().recoveryActionCount()
                )
        );
    }
}