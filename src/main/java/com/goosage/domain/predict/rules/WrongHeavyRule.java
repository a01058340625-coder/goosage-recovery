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
        return 9;
    }

    @Override
    public boolean matches(RecoverySnapshot s) {
        if (s == null || s.state() == null) {
            return false;
        }

        int attempts = s.state().betAttempts();
        int relapse = s.state().relapseSignalCount();
        int blocked = s.state().betBlockedCount();
        int recovery = s.state().recoveryActionCount();

        // Recovery 도메인 기준:
        // 단순 차단 성공 / 회복 진행은 여기서 잡지 않음
        if (relapse <= 0) {
            return false;
        }

        // 회복 행동이 충분히 앞서는 경우는 RecoveryProgress / RecoverySafe로 넘김
        if (recovery > relapse && recovery >= attempts) {
            return false;
        }

        // blocked만 있는 경우는 relapse-heavy가 아님
        if (blocked > 0 && relapse == 0) {
            return false;
        }

        // 진짜 위험 누적 상태만 여기서 처리
        return relapse >= 2 || (relapse >= 1 && attempts >= 2);
    }

    @Override
    public Prediction apply(RecoverySnapshot s) {
        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.WRONG_HEAVY,
                "위험 신호가 많이 쌓였어. 새 행동보다 회복 행동을 먼저 하자.",
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