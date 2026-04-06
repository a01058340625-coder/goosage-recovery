package com.goosage.domain.predict;

public enum PredictionReasonCode {
    DATA_POOR,
    LOW_ACTIVITY_3D,

    // 기존 REVIEW_WRONG_PENDING → 의미 유지 (이름은 나중에 바꿔도 됨)
    REVIEW_WRONG_PENDING,

    RECOVERY_PROGRESS,
    RECOVERY_SAFE,

    // 기존 LOW_QUALITY_OPEN → 의미 유지
    LOW_QUALITY_OPEN,

    GOOD_PROGRESS,
    HABIT_STABLE,
    MINIMUM_ACTION,
    HABIT_COLLAPSE,

    // 기존 TODAY_DONE → 의미 유지
    TODAY_DONE,

    WRONG_HEAVY,
    STABLE_PROGRESS,

    // 🔥 추가 (recovery 전용)
    URGE_HIGH,
    RELAPSE_RISK
}