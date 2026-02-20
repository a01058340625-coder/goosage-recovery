package com.goosage.domain.predict.rules;

import java.util.Map;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.study.StudySnapshot;
import org.springframework.stereotype.Component;

@Component

public class DataPoorDefaultRule implements PredictionRule {

    @Override
    public int priority() {
        return 5;
    }

    @Override
    public boolean matches(StudySnapshot s) {
        // fresh/empty user 판단: 최근 3일 이벤트 0 + streak 0
        return s.recentEventCount3d() == 0 && s.streakDays() == 0;
    }

    @Override
    public Prediction apply(StudySnapshot s) {
        return Prediction.of(
            PredictionLevel.WARNING,                 // (너가 Level을 SAFE/WARNING/DANGER로 했다면 WARNING)
            PredictionReasonCode.DATA_POOR,
            "학습 데이터가 부족해. 일단 최소 1개 이벤트부터 쌓아보자.",
            Map.of(
                "recentEventCount3d", s.recentEventCount3d(),
                "streakDays", s.streakDays(),
                "daysSinceLastEvent", s.daysSinceLastEvent(),
                "lastEventAt", s.lastEventAt()
            )
        );
    }
}