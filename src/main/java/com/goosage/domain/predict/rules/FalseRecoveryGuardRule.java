package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.study.StudySnapshot;

@Component
public class FalseRecoveryGuardRule implements PredictionRule {

    private static final int EVENTS_MIN = 5;
    private static final double OPEN_RATIO_MAX = 0.55;
    private static final double QUIZ_RATIO_MIN = 0.30;

    @Override
    public int priority() {
        // RecoverySafeRule(15)보다 먼저 막아야 함
        return 14;
    }

    @Override
    public boolean matches(StudySnapshot s) {
        int events = s.state().eventsCount();
        int quiz = s.state().quizSubmits();
        int wrong = s.state().wrongReviews();
        int wrongDone = s.state().wrongReviewDoneCount();

        if (events < EVENTS_MIN) {
            return false;
        }

        if (wrongDone <= 0) {
            return false;
        }

        double openRatio = events <= 0 ? 0.0 : (double) s.state().justOpenCount() / events;
        double quizRatio = events <= 0 ? 0.0 : (double) quiz / events;

        boolean noWrongContext = wrong == 0;
        boolean lowQuizQuality = quizRatio < QUIZ_RATIO_MIN;
        boolean openHeavy = openRatio > OPEN_RATIO_MAX;

        // DONE는 있는데 실제 wrong 맥락이 빈약하고,
        // 동시에 품질도 낮으면 가짜 회복으로 본다.
        return noWrongContext && (lowQuizQuality || openHeavy);
    }

    @Override
    public Prediction apply(StudySnapshot s) {
        int events = s.state().eventsCount();
        int quiz = s.state().quizSubmits();
        int wrong = s.state().wrongReviews();
        int wrongDone = s.state().wrongReviewDoneCount();

        double openRatio = events <= 0 ? 0.0 : (double) s.state().justOpenCount() / events;
        double quizRatio = events <= 0 ? 0.0 : (double) quiz / events;

        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.RECOVERY_PROGRESS,
                "복습 완료가 일부 쌓였지만 실제 회복으로 보기엔 학습 맥락이 아직 약하다. 퀴즈 1개로 회복 흐름을 확인하자.",
                Map.of(
                        "eventsCount", events,
                        "quizSubmits", quiz,
                        "wrongReviews", wrong,
                        "wrongReviewDoneCount", wrongDone,
                        "openRatio", openRatio,
                        "quizRatio", quizRatio,
                        "openRatioMax", OPEN_RATIO_MAX,
                        "quizRatioMin", QUIZ_RATIO_MIN
                )
        );
    }
}