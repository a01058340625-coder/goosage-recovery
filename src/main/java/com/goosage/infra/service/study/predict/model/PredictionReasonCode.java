package com.goosage.infra.service.study.predict.model;

public enum PredictionReasonCode {

    DATA_POOR("데이터가 충분하지 않아 보수적으로 판단함"),
    GAP_4DAYS("최근 4일 이상 학습 공백이 발생함"),
    GAP_3DAYS("최근 3일 연속 학습 공백이 발생함"),
    STABLE("학습 상태가 안정적으로 유지되고 있음"),
    TODAY_DONE("오늘 학습을 이미 완료함"),
    LOW_ACTIVITY_3D("최근 3일 활동이 낮음"),
    DEFAULT_FALLBACK("기본 안전 판단 적용");
	

    private final String description;

    PredictionReasonCode(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
