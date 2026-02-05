package com.goosage.dto.study;

public class StudyCoachResponse {

    private StudyStateView state;
    private NextActionView nextAction;
    private PredictionDto prediction;

    public StudyStateView getState() {
        return state;
    }

    public void setState(StudyStateView state) {
        this.state = state;
    }

    public NextActionView getNextAction() {
        return nextAction;
    }

    public void setNextAction(NextActionView nextAction) {
        this.nextAction = nextAction;
    }

    public PredictionDto getPrediction() {
        return prediction;
    }

    public void setPrediction(PredictionDto prediction) {
        this.prediction = prediction;
    }
}
