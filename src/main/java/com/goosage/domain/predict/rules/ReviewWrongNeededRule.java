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

        if (!s.studiedToday()) {
            return false;
        }

        int blocked = s.state().betBlockedCount();
        int recovery = s.state().recoveryActionCount();

        // blocked 중심의 pending 시나리오 전용
        return blocked > 0 && recovery == 0;
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