package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.study.StudySnapshot;

@Component

public class LowActivity3dRule implements PredictionRule {

    @Override public int priority() { return 30; }

    @Override
    public boolean matches(StudySnapshot s) {
    	return !s.studiedToday()
    		    && s.recentEventCount3d() <= 1
    		    && !(s.recentEventCount3d() == 0 && s.streakDays() == 0); // DATA_POOR 제외
    }

    @Override
    public Prediction apply(StudySnapshot s) {
        return Prediction.of(
            PredictionLevel.WARNING,
            PredictionReasonCode.LOW_ACTIVITY_3D,
            "최근 3일 학습 활동이 낮아. 오늘 최소 1개 이벤트만 만들어보자.",
            Map.of(
                "studiedToday", s.studiedToday(),
                "recentEventCount3d", s.recentEventCount3d(),
                "daysSinceLastEvent", s.daysSinceLastEvent(),
                "streakDays", s.streakDays()
            )
        );
    }
}