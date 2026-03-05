package com.goosage.domain.predict.rules;

import java.util.Map;
import org.springframework.stereotype.Component;

import com.goosage.domain.predict.*;
import com.goosage.domain.study.StudySnapshot;

@Component
public class DefaultFallbackRule implements PredictionRule {

    @Override public int priority() { return 999; }

    @Override
    public boolean matches(StudySnapshot s) {
        return true;
    }

    @Override
    public Prediction apply(StudySnapshot s) {
        return Prediction.of(
            PredictionLevel.WARNING,
            PredictionReasonCode.DEFAULT_FALLBACK,
            "판정 규칙에 걸리지 않았어. 오늘 최소 1개 이벤트만 만들어보자.",
            Map.of(
                "streakDays", s.streakDays(),
                "daysSinceLastEvent", s.daysSinceLastEvent(),
                "recentEventCount3d", s.recentEventCount3d(),
                "eventsCount", s.state().eventsCount()
            )
        );
    }
}