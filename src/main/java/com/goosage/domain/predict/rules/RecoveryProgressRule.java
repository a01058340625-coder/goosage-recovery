package com.goosage.domain.predict.rules;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.study.StudySnapshot;

@Component
public class RecoveryProgressRule implements PredictionRule {

	@Override
	public int priority() {
	    return 5;
	}

	@Override
	public boolean matches(StudySnapshot s) {
	    return s != null
	            && s.state() != null
	            && s.studiedToday()
	            && s.state().wrongReviewDoneCount() > s.state().wrongReviews();
	}

    @Override
    public Prediction apply(StudySnapshot s) {
        return new Prediction(
            PredictionLevel.WARNING,
            PredictionReasonCode.RECOVERY_PROGRESS,
            "오답을 다시 정리하며 회복 중이야. 흐름을 이어가자.",
            java.util.Map.of(
                "streakDays", s.streakDays(),
                "daysSinceLastEvent", s.daysSinceLastEvent(),
                "recentEventCount3d", s.recentEventCount3d(),
                "eventsCount", s.state() != null ? s.state().eventsCount() : 0,
                "wrongReviews", s.state() != null ? s.state().wrongReviews() : 0,
                "wrongReviewDoneCount", s.state() != null ? s.state().wrongReviewDoneCount() : 0
            )
        );
    }
}