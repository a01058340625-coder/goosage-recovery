package com.goosage.domain.predict;

import com.goosage.domain.study.StudySnapshot;

public interface PredictionRule {
    int priority();                 // 낮을수록 먼저 적용
    boolean matches(StudySnapshot s);
    Prediction apply(StudySnapshot s);
}
