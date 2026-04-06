package com.goosage.domain.predict.rules;

import static java.util.Map.entry;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.recovery.RecoverySnapshot;

@Component
public class RecoverySafeRule implements PredictionRule {

    private static final int EVENTS_MIN = 5;
    private static final int WRONG_DONE_MIN = 2;
    private static final int QUIZ_MIN = 2;
    private static final int RECENT_3D_MIN = 3;
    private static final double OPEN_RATIO_MAX = 0.55;
    private static final double QUIZ_RATIO_MIN = 0.25;

    @Override
    public int priority() {
        return 15;
    }

    @Override
    public boolean matches(RecoverySnapshot s) {
        if (s == null || s.state() == null) {
            return false;
        }

        if (!s.studiedToday()) {
            return false;
        }

        int events = s.state().eventsCount();
        int quiz = s.state().quizSubmits();
        int wrong = s.state().wrongReviews();
        int done = s.state().wrongReviewDoneCount();
        int justOpen = s.state().justOpenCount();

        if (wrong != 0) {
            return false;
        }

        if (events < EVENTS_MIN) {
            return false;
        }

        if (done < WRONG_DONE_MIN) {
            return false;
        }

        if (quiz < QUIZ_MIN) {
            return false;
        }

        if (s.recentEventCount3d() < RECENT_3D_MIN) {
            return false;
        }

        double openRatio = events <= 0 ? 0.0 : (double) justOpen / events;
        double quizRatio = events <= 0 ? 0.0 : (double) quiz / events;

        if (openRatio > OPEN_RATIO_MAX) {
            return false;
        }

        if (quizRatio < QUIZ_RATIO_MIN) {
            return false;
        }

        return true;
    }

    @Override
    public Prediction apply(RecoverySnapshot s) {
        int events = s.state().eventsCount();
        int quiz = s.state().quizSubmits();
        int wrong = s.state().wrongReviews();
        int done = s.state().wrongReviewDoneCount();
        int justOpen = s.state().justOpenCount();

        double openRatio = events <= 0 ? 0.0 : (double) justOpen / events;
        double quizRatio = events <= 0 ? 0.0 : (double) quiz / events;

        return Prediction.of(
                PredictionLevel.SAFE,
                PredictionReasonCode.RECOVERY_SAFE,
                "복습 완료와 퀴즈 흐름이 함께 유지되어 회복 안정권에 들어왔다.",
                Map.ofEntries(
                        entry("streakDays", s.streakDays()),
                        entry("daysSinceLastEvent", s.daysSinceLastEvent()),
                        entry("recentEventCount3d", s.recentEventCount3d()),
                        entry("eventsCount", events),
                        entry("quizSubmits", quiz),
                        entry("wrongReviews", wrong),
                        entry("wrongReviewDoneCount", done),
                        entry("studiedToday", s.studiedToday()),
                        entry("quizMin", QUIZ_MIN),
                        entry("wrongDoneMin", WRONG_DONE_MIN),
                        entry("quizRatio", quizRatio),
                        entry("openRatio", openRatio),
                        entry("quizRatioMin", QUIZ_RATIO_MIN),
                        entry("openRatioMax", OPEN_RATIO_MAX)
                )
        );
    }
}