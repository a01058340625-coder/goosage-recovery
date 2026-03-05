package com.goosage.api.view.study;

public enum CoachPredictionReasonCode {

    TODAY_DONE("오늘 학습을 이미 완료함"),
    DATA_POOR("학습 데이터가 부족해 보수적으로 판단함"),
    LOW_ACTIVITY_3D("최근 3일 학습 활동이 낮음"),
    HABIT_COLLAPSE("학습 공백이 길어져 흐름 붕괴 위험"),
    DEFAULT_FALLBACK("기본 안전 판단 적용");

    private final String description;

    CoachPredictionReasonCode(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}