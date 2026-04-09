package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.recovery.RecoverySnapshot;

@Component
public class GoodProgressRule implements PredictionRule {

    @Override
    public int priority() {
        return 60;
    }

    @Override
    public boolean matches(RecoverySnapshot s) {
        if (s == null || s.state() == null) {
            return false;
        }

        if (!s.studiedToday()) {
            return false;
        }

        // recovery 도메인에서는 relapse/attempt/recovery 컨텍스트가 있으면
        // 일반 GOOD_PROGRESS로 보내지 않는다.
        if (s.state().betAttempts() > 0) {
            return false;
        }

        if (s.state().relapseSignalCount() > 0) {
            return false;
        }

        if (s.state().recoveryActionCount() > 0) {
            return false;
        }

        return s.state().eventsCount() >= 5
                && s.urgeRatio() >= 0.5;
    }

    @Override
    public Prediction apply(RecoverySnapshot s) {
        return Prediction.of(
                PredictionLevel.SAFE,
                PredictionReasonCode.GOOD_PROGRESS,
                "위험 신호 없이 안정적으로 기록 흐름이 유지되고 있다.",
                Map.of(
                        "urgeRatio", s.urgeRatio(),
                        "eventsCount", s.state().eventsCount(),
                        "urgeLogs", s.state().urgeLogs(),
                        "betAttempts", s.state().betAttempts(),
                        "recoveryActionCount", s.state().recoveryActionCount(),
                        "relapseSignalCount", s.state().relapseSignalCount()
                )
        );
    }
}