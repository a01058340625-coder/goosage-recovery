package com.goosage.api.view.study;

import java.util.Map;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;

public final class PredictionMapper {

    private PredictionMapper() {}

    public static CoachPredictionView toCoachView(Prediction d) {
        var level = toCoachLevel(d.level());
        var reason = toCoachReason(d.reasonCode());
        var evidence = toEvidence(d.evidence());
        return CoachPredictionView.of(level, reason, evidence);
    }

    private static CoachPredictionLevel toCoachLevel(PredictionLevel l) {
        return switch (l) {
            case SAFE -> CoachPredictionLevel.SAFE;
            case WARNING, DANGER -> CoachPredictionLevel.RISK;
        };
    }

    private static CoachPredictionReasonCode toCoachReason(PredictionReasonCode c) {
        return switch (c) {
            case TODAY_DONE -> CoachPredictionReasonCode.TODAY_DONE;
            case DATA_POOR -> CoachPredictionReasonCode.DATA_POOR;
            case LOW_ACTIVITY_3D -> CoachPredictionReasonCode.STABLE; // 임시
            default -> CoachPredictionReasonCode.DEFAULT_FALLBACK;
        };
    }

    private static PredictionEvidence toEvidence(Map<String, Object> m) {
        int streakDays = getInt(m, "streakDays", 0);
        int daysSinceLastEvent = getInt(m, "daysSinceLastEvent", 0);
        int recentEventCount3d = getInt(m, "recentEventCount3d", 0);
        return new PredictionEvidence(streakDays, daysSinceLastEvent, recentEventCount3d);
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