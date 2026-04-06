package com.goosage.domain;

public enum NextActionType {

    // 위험 신호 1건 처리
    PROCESS_RISK_SIGNAL,

    // 상태 점검 / 회복 체크
    RECOVERY_CHECK,

    // 회복 행동 1회 실행
    DO_RECOVERY_ACTION,

    // 최소 접촉 유지
    MINIMUM_CONTACT,

    // 오늘 방어 완료
    TODAY_SAFE
}