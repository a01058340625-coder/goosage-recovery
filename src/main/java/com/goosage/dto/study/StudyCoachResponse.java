package com.goosage.dto.study;

import com.goosage.service.study.action.NextActionDto;
import com.goosage.service.study.dto.StudyStateDto;

public class StudyCoachResponse {

    private StudyStateDto state;
    private NextActionDto nextAction;
    private PredictionDto prediction;

    public StudyStateDto getState() {
        return state;
    }

    public void setState(StudyStateDto state) {
        this.state = state;
    }

    public NextActionDto getNextAction() {
        return nextAction;
    }

    public void setNextAction(NextActionDto nextAction) {
        this.nextAction = nextAction;
    }

    public PredictionDto getPrediction() {
        return prediction;
    }

    public void setPrediction(PredictionDto prediction) {
        this.prediction = prediction;
    }
}
