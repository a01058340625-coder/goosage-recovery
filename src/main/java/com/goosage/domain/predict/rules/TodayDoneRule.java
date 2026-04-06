package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;
import static java.util.Map.entry;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.recovery.RecoverySnapshot;

@Component
public class TodayDoneRule implements PredictionRule {

    private static final int ACTION_MIN = 5;
    private static final double ACTION_RATIO_MIN = 0.60;
    private static final double PASSIVE_RATIO_MAX = 0.40;

    @Override
    public int priority() {
        return 5;
    }

    @Override
    public boolean matches(RecoverySnapshot s) {
        if (!s.studiedToday()) {
            return false;
        }

        int action = s.state().quizSubmits();
        int risk = s.state().wrongReviews();

        double actionRatio = s.quizRatio();
        double passiveRatio = s.openRatio();

        return action >= ACTION_MIN
                && risk == 0
                && actionRatio >= ACTION_RATIO_MIN
                && passiveRatio <= PASSIVE_RATIO_MAX;
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
                        entry("actionCount", s.state().quizSubmits()),
                        entry("riskSignal", s.state().wrongReviews()),
                        entry("studiedToday", s.studiedToday()),
                        entry("actionMin", ACTION_MIN),
                        entry("actionRatio", s.quizRatio()),
                        entry("passiveRatio", s.openRatio()),
                        entry("actionRatioMin", ACTION_RATIO_MIN),
                        entry("passiveRatioMax", PASSIVE_RATIO_MAX)
                )
        );
    }
}