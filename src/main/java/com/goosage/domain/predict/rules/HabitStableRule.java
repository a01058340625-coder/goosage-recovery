package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.recovery.RecoverySnapshot;

@Component
public class HabitStableRule implements PredictionRule {

    @Override
    public int priority() {
        return 25;
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
        int relapse = s.state().relapseSignalCount();
        int recovery = s.state().recoveryActionCount();
        int blocked = s.state().betBlockedCount();

        // recovery-safe 케이스는 더 상위/별도 rule에서 처리
        if (recovery > 0 && recovery > (attempts + relapse)) {
            return false;
        }

        // 충동이 아직 있으면 stable로 보지 않음
        if (urge > 0) {
            return false;
        }

        // 시도/재발 신호가 있으면 stable 금지
        if (attempts > 0 || relapse > 0) {
            return false;
        }

        // blocked만 있고 recovery 정리가 없으면 아직 안정 아님
        if (blocked > 0 && recovery == 0) {
            return false;
        }

        // 진짜 stable은 recovery 흐름이 어느 정도 누적된 상태만 허용
        if (recovery <= 0) {
            return false;
        }

        return s.streakDays() >= 3
                && s.recentEventCount3d() >= 3
                && s.daysSinceLastEvent() == 0;
    }

    @Override
    public Prediction apply(RecoverySnapshot s) {
        return Prediction.of(
                PredictionLevel.SAFE,
                PredictionReasonCode.HABIT_STABLE,
                "행동 습관이 안정화되고 있다. 지금 리듬을 유지하자.",
                Map.of(
                        "streakDays", s.streakDays(),
                        "recentEventCount3d", s.recentEventCount3d(),
                        "daysSinceLastEvent", s.daysSinceLastEvent(),
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