package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;
import static java.util.Map.entry;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.study.StudySnapshot;

@Component
public class TodayDoneRule implements PredictionRule {

    private static final int QUIZ_MIN = 5;
    private static final double QUIZ_RATIO_MIN = 0.60;
    private static final double OPEN_RATIO_MAX = 0.40;

    @Override
    public int priority() {
        return 5;
    }

    @Override
    public boolean matches(StudySnapshot s) {
        if (!s.studiedToday()) {
            return false;
        }

        int quiz = s.state().quizSubmits();
        int wrong = s.state().wrongReviews();

        double quizRatio = s.quizRatio();
        double openRatio = s.openRatio();

        return quiz >= QUIZ_MIN
                && wrong == 0
                && quizRatio >= QUIZ_RATIO_MIN
                && openRatio <= OPEN_RATIO_MAX;
    }

    @Override
    public Prediction apply(StudySnapshot s) {
        return Prediction.of(
                PredictionLevel.SAFE,
                PredictionReasonCode.TODAY_DONE,
                "오늘 학습은 충분히 완료됐다. 현재 흐름을 유지하자.",
                Map.ofEntries(
                	    entry("streakDays", s.streakDays()),
                	    entry("daysSinceLastEvent", s.daysSinceLastEvent()),
                	    entry("recentEventCount3d", s.recentEventCount3d()),
                	    entry("eventsCount", s.state().eventsCount()),
                	    entry("quizSubmits", s.state().quizSubmits()),
                	    entry("wrongReviews", s.state().wrongReviews()),
                	    entry("studiedToday", s.studiedToday()),
                	    entry("quizMin", QUIZ_MIN),
                	    entry("quizRatio", s.quizRatio()),
                	    entry("openRatio", s.openRatio()),
                	    entry("quizRatioMin", QUIZ_RATIO_MIN),
                	    entry("openRatioMax", OPEN_RATIO_MAX)
                	)
        );
    }
}