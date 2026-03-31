package com.goosage.api.view.study;

public enum CoachPredictionReasonCode {
    TODAY_DONE("오늘 학습을 이미 완료함"),
    REVIEW_WRONG_PENDING("오늘 학습은 충분하지만 오답 복습이 남아 있음"),
    DATA_POOR("학습 데이터가 부족해 보수적으로 판단함"),
    LOW_ACTIVITY_3D("최근 3일 학습 활동이 낮음"),
    HABIT_COLLAPSE("학습 공백이 길어져 흐름 붕괴 위험"),

    HABIT_STABLE("학습 습관이 안정적으로 유지되고 있음"),
    WRONG_HEAVY("오답 비중이 높아 오답 복습이 우선임"),
    LOW_QUALITY_OPEN("열기만 반복되고 있어 학습 전환이 필요함"),
    MINIMUM_ACTION("최소 행동부터 다시 시작해야 함"),

    RECOVERY_PROGRESS("복귀는 시작됐지만 아직 더 학습이 필요함"),
    RECOVERY_SAFE("최소 복구를 넘어 안정권에 진입함"),

    DEFAULT_FALLBACK("기본 안전 판단 적용");

    private final String description;

    CoachPredictionReasonCode(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}