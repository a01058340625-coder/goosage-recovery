package com.goosage.domain.predict;

import java.util.Map;

/**
 * Domain Prediction
 * - RuleEngine의 결과물 (infra DTO와 분리)
 * - message: 사람이 읽는 한 줄 근거(WHY)
 * - evidence: 룰이 남기는 key-value 근거(설명가능)
 */
public record Prediction(
        PredictionLevel level,
        PredictionReasonCode reasonCode,
        String message,
        Map<String, Object> evidence
) {
    // ✅ 룰에서 쓰는 4개 인자 버전 (level, reason, message, evidence)
    public static Prediction of(
            PredictionLevel level,
            PredictionReasonCode reasonCode,
            String message,
            Map<String, Object> evidence
    ) {
        return new Prediction(level, reasonCode, message, evidence);
    }

    // ✅ 편의: message 없이도 만들 수 있게(기존 코드 호환용)
    public static Prediction of(
            PredictionLevel level,
            PredictionReasonCode reasonCode,
            Map<String, Object> evidence
    ) {
        return new Prediction(level, reasonCode, null, evidence);
    }

    public Prediction withEvidence(Map<String, Object> newEvidence) {
        return new Prediction(level, reasonCode, message, newEvidence);
    }
}