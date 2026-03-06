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

    // DATA_POOR 다음
    @Override
    public int priority() {
        return 30;
    }

    @Override
    public boolean matches(StudySnapshot s) {
        // 오늘 이미 공부했으면 "3일 저활동" 경고 대상은 아님
        if (s.studiedToday()) {
            return false;
        }

        // DATA_POOR(0/0)와 구분
        return s.recentEventCount3d() <= 1
                && !(s.recentEventCount3d() == 0 && s.streakDays() == 0);
    }

    @Override
    public Prediction apply(StudySnapshot s) {
        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.LOW_ACTIVITY_3D,
                "최근 3일 학습 활동이 낮다. 오늘 최소 1개 이벤트부터 다시 만들자.",
                Map.of(
                        "studiedToday", s.studiedToday(),
                        "recentEventCount3d", s.recentEventCount3d(),
                        "daysSinceLastEvent", s.daysSinceLastEvent(),
                        "streakDays", s.streakDays()
                )
        );
    }
}