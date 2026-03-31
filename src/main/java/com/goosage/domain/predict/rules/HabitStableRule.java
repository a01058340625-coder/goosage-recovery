package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.study.StudySnapshot;

@Component
public class HabitStableRule implements PredictionRule {

    @Override
    public int priority() {
        return 25;
    }

    @Override
    public boolean matches(StudySnapshot s) {
        if (s == null || s.state() == null) {
            return false;
        }

        if (!s.studiedToday()) {
            return false;
        }

        return s.studiedToday()
        	    && s.streakDays() >= 3
        	    && s.recentEventCount3d() >= 3;
    } 	    
        	           	   
    @Override
    public Prediction apply(StudySnapshot s) {
        return Prediction.of(
                PredictionLevel.SAFE,
                PredictionReasonCode.HABIT_STABLE,
                "학습 습관이 안정화되고 있다. 지금 리듬을 유지하자.",
                Map.of(
                        "streakDays", s.streakDays(),
                        "recentEventCount3d", s.recentEventCount3d(),
                        "daysSinceLastEvent", s.daysSinceLastEvent(),
                        "eventsCount", s.state() != null ? s.state().eventsCount() : 0,
                        "quizSubmits", s.state() != null ? s.state().quizSubmits() : 0
                )
        );
    }
}