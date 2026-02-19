package com.goosage.domain.predict.rules;

import java.util.Map;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.study.StudySnapshot;

public class TodayDoneRule implements PredictionRule {

    @Override public int priority() { return 10; }

    @Override
    public boolean matches(StudySnapshot s) {
        return s.studiedToday();
    }

    @Override
    public Prediction apply(StudySnapshot s) {
        return new Prediction(
            PredictionLevel.SAFE,
            PredictionReasonCode.TODAY_DONE,
            Map.of(
                "studiedToday", s.studiedToday(),
                "eventsCount", s.state().eventsCount()
            )
        );
    }
}
