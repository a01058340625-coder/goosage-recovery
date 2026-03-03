package com.goosage.infra.service.study.predict;

import java.util.Map;

public final class PredictionMapper {

    private PredictionMapper() {}

    public static com.goosage.infra.service.study.predict.model.InfraPredictionView toInfra(
            com.goosage.domain.predict.Prediction d
    ) {
        var level = toInfraLevel(d.level());
        var reason = toInfraReason(d.reasonCode());
        var evidence = toInfraEvidence(d.evidence());

        return com.goosage.infra.service.study.predict.model.InfraPredictionView.of(level, reason, evidence);
    }

    private static com.goosage.infra.service.study.predict.model.InfraPredictionLevelView toInfraLevel(
            com.goosage.domain.predict.PredictionLevel l
    ) {
        // infra는 SAFE / RISK 두 단계였다면 이렇게 매핑
        // WARNING/DANGER 모두 RISK로 떨어뜨리는 게 “보수적”이라 안전함
        return switch (l) {
            case SAFE -> com.goosage.infra.service.study.predict.model.InfraPredictionLevelView.SAFE;
            case WARNING, DANGER -> com.goosage.infra.service.study.predict.model.InfraPredictionLevelView.RISK;
        };
    }

    private static com.goosage.infra.service.study.predict.model.InfraPredictionReasonCodeView toInfraReason(
            com.goosage.domain.predict.PredictionReasonCode c
    ) {
        // ✅ infra enum에 LOW_ACTIVITY_3D가 없으면 일단 STABLE로 매핑(임시)
        //    가장 정공법은 infra enum에도 LOW_ACTIVITY_3D 추가하는 것(아래에 적어둠)
        return switch (c) {
            case TODAY_DONE -> com.goosage.infra.service.study.predict.model.InfraPredictionReasonCodeView.TODAY_DONE;
            case DATA_POOR -> com.goosage.infra.service.study.predict.model.InfraPredictionReasonCodeView.DATA_POOR;
            case LOW_ACTIVITY_3D -> com.goosage.infra.service.study.predict.model.InfraPredictionReasonCodeView.STABLE; // 임시
            default -> com.goosage.infra.service.study.predict.model.InfraPredictionReasonCodeView.DEFAULT_FALLBACK;
        };
    }

    private static com.goosage.infra.service.study.predict.model.PredictionEvidence toInfraEvidence(
            Map<String, Object> m
    ) {
        int streakDays = getInt(m, "streakDays", 0);
        int daysSinceLastEvent = getInt(m, "daysSinceLastEvent", 0);
        int recentEventCount3d = getInt(m, "recentEventCount3d", 0);

        return new com.goosage.infra.service.study.predict.model.PredictionEvidence(
                streakDays, daysSinceLastEvent, recentEventCount3d
        );
    }

    private static int getInt(Map<String, Object> m, String key, int defaultValue) {
        if (m == null) return defaultValue;
        Object v = m.get(key);
        if (v == null) return defaultValue;
        if (v instanceof Integer i) return i;
        if (v instanceof Long l) return (int) l.longValue();
        if (v instanceof String s) {
            try { return Integer.parseInt(s); } catch (Exception ignored) {}
        }
        return defaultValue;
    }
}