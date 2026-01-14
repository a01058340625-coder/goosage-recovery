package com.goosage.dto;

public class QuizResultItem {

    private int questionId;
    private String questionText;
    private String userAnswer;
    private boolean correct;

    public QuizResultItem() {
        // 기본 생성자 (Jackson / JPA / setter 방식 대비)
    }

    public QuizResultItem(int questionId, String questionText, String userAnswer, boolean correct) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.userAnswer = userAnswer;
        this.correct = correct;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}
