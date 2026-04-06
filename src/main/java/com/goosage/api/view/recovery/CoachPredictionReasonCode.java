package com.goosage.api.view.recovery;

public enum CoachPredictionReasonCode {

    DATA_POOR("데이터가 부족해 현재 상태를 보수적으로 판단한다"),

    LOW_ACTIVITY_3D("최근 회복 행동이 부족하다"),
    HABIT_COLLAPSE("회복 루틴이 무너진 상태다"),

    REVIEW_WRONG_PENDING("위험 신호가 아직 처리되지 않았다"),
    WRONG_HEAVY("위험 신호가 과도하게 누적되고 있다"),

    LOW_QUALITY_OPEN("접속은 했지만 행동으로 이어지지 않았다"),

    MINIMUM_ACTION("최소 행동으로 흐름을 다시 시작해야 한다"),

    RECOVERY_PROGRESS("회복 흐름이 다시 형성되고 있다"),
    RECOVERY_SAFE("회복 상태가 안정적으로 유지되고 있다"),

    HABIT_STABLE("회복 루틴이 안정적으로 유지되고 있다"),
    GOOD_PROGRESS("회복 행동이 잘 유지되고 있다"),
    STABLE_PROGRESS("안정적인 회복 흐름이 이어지고 있다"),

    TODAY_DONE("오늘 회복 행동이 충분히 수행되었다"),

    URGE_HIGH("충동 강도가 높은 상태다"),
    RELAPSE_RISK("재발 위험 구간이다"),

    DEFAULT_FALLBACK("기본 안전 판단");

    private final String description;

    CoachPredictionReasonCode(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}