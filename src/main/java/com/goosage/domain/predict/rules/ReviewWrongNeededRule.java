package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.study.StudySnapshot;

@Component
public class ReviewWrongNeededRule implements PredictionRule {

    private static final int QUIZ_MIN = 5;

    @Override
    public int priority() {
        // TodayDoneRule(5) 다음
        return 10;
    }

    @Override
    public boolean matches(StudySnapshot s) {
        if (!s.studiedToday()) {
            return false;
        }

        int quiz = s.state().quizSubmits();
        int wrong = s.state().wrongReviews();

        return quiz >= QUIZ_MIN && wrong > 0;
    }

    @Override
    public Prediction apply(StudySnapshot s) {
        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.REVIEW_WRONG_PENDING,
                "오늘 학습량은 충분하지만 오답 복습이 남아 있다. REVIEW_WRONG부터 정리하자.",
                Map.of(
                        "studiedToday", s.studiedToday(),
                        "streakDays", s.streakDays(),
                        "daysSinceLastEvent", s.daysSinceLastEvent(),
                        "recentEventCount3d", s.recentEventCount3d(),
                        "eventsCount", s.state().eventsCount(),
                        "quizSubmits", s.state().quizSubmits(),
                        "wrongReviews", s.state().wrongReviews(),
                        "quizMin", QUIZ_MIN
                )
        );
    }
}