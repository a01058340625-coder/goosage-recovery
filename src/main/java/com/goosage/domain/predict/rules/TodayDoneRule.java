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

    private static final int QUIZ_MIN = 5;

    @Override
    public int priority() { return 10; }

    @Override
    public boolean matches(StudySnapshot s) {
        int quiz  = s.state().quizSubmits();
        int wrong = s.state().wrongReviews();
        return quiz >= QUIZ_MIN && wrong == 0;
    }

    @Override
    public Prediction apply(StudySnapshot s) {
        return Prediction.of(
            PredictionLevel.SAFE,
            PredictionReasonCode.TODAY_DONE,
            Map.of(
                "streakDays", s.streakDays(),
                "daysSinceLastEvent", s.daysSinceLastEvent(),
                "recentEventCount3d", s.recentEventCount3d(),

                "eventsCount", s.state().eventsCount(),
                "quizSubmits", s.state().quizSubmits(),
                "wrongReviews", s.state().wrongReviews(),
                "studiedToday", s.studiedToday(),

                "quizMin", QUIZ_MIN
            )
        );
    }
}