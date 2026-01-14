package com.goosage.dto;

import java.util.ArrayList;
import java.util.List;

public class QuizSubmitRequest {

    private List<QuizAnswer> answers = new ArrayList<>();

    public List<QuizAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<QuizAnswer> answers) {
        this.answers = answers;
    }
}
