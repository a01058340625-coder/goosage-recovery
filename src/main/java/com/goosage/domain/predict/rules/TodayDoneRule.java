package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.study.StudySnapshot;

@Component
public class TodayDoneRule implements PredictionRule {

    @Override
    public int priority() { return 10; }

    @Override
    public boolean matches(StudySnapshot s) {
        return s.studiedToday();
    }

    @Override
    public Prediction apply(StudySnapshot s) {
        return Prediction.of(
            PredictionLevel.SAFE,
            PredictionReasonCode.TODAY_DONE,
            Map.of(
                // ✅ 핵심 3키 (infra Evidence로 내려갈 값)
                "streakDays", s.streakDays(),
                "daysSinceLastEvent", s.daysSinceLastEvent(),
                "recentEventCount3d", s.recentEventCount3d(),

                // (추가 근거)
                "eventsCount", s.state().eventsCount(),
                "studiedToday", s.studiedToday()
            )
        );
    }
}