package com.goosage.api.view.study;

// 필요하면 남기고, 아니면 삭제(동일 패키지면 import 자체 불필요)
import com.goosage.api.view.study.NextActionView;
import com.goosage.api.view.study.StudyStateView;

public record StudyCoachResponse(
        StudyStateView state,
        NextActionView next,
        StudyPredictionView prediction
) {}
