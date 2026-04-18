package com.goosage.domain.predict.vector;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class VectorMatcher {

    public BehaviorPattern match(ObservationVector v) {
        // urge-only 위험 재진입 케이스는 WRONG_HEAVY로 고정
        if (v.urgeRatio() > 0.0
                && v.attemptRatio() == 0.0
                && v.relapseRatio() == 0.0
                && v.recoveryRatio() > 0.0) {
            return BehaviorPattern.WRONG_HEAVY;
        }

        Map<BehaviorPattern, ObservationVector> targets = targetVectors();

        BehaviorPattern best = null;
        double bestDistance = Double.MAX_VALUE;

        for (Map.Entry<BehaviorPattern, ObservationVector> entry : targets.entrySet()) {
            BehaviorPattern pattern = entry.getKey();

            // 위험 신호가 있으면 안정/진행/저활동 계열 후보 제거
            if (hasRiskSignal(v)
                    && (pattern == BehaviorPattern.RECOVERY_PROGRESS
                    || pattern == BehaviorPattern.QUIZ_ONLY
                    || pattern == BehaviorPattern.HABIT_STABLE
                    || pattern == BehaviorPattern.LOW_ACTIVITY)) {
                continue;
            }

            // HABIT_STABLE은 단순 거리만으로 뽑지 않고 진입 조건을 둔다.
            if (pattern == BehaviorPattern.HABIT_STABLE && !canBeHabitStable(v)) {
                continue;
            }

            double d = distance(v, entry.getValue());
            if (d < bestDistance) {
                bestDistance = d;
                best = pattern;
            }
        }

        // 모든 후보가 제거되는 극단 상황 방어
        if (best == null) {
            return BehaviorPattern.LOW_ACTIVITY;
        }

        return best;
    }

    public double distanceTo(ObservationVector actual, BehaviorPattern pattern) {
        return distance(actual, targetVectors().get(pattern));
    }

    private boolean hasRiskSignal(ObservationVector v) {
        return v.urgeRatio() > 0.0
                || v.attemptRatio() > 0.0
                || v.relapseRatio() > 0.0;
    }

    /**
     * Recovery 도메인에서 HABIT_STABLE은
     * "위험 신호 없이 회복 루틴이 충분히 반복되어 안정권에 들어온 상태"로 본다.
     *
     * Day50 기준 의도:
     * - final_safe_dense / final_stable_dense 는 통과
     * - safe_thin / progress_dense / defense_bridge / long_defense_timeline 은 제외
     */
    private boolean canBeHabitStable(ObservationVector v) {
        return v.urgeRatio() == 0.0
                && v.attemptRatio() == 0.0
                && v.relapseRatio() == 0.0
                && v.blockedRatio() == 0.0
                && v.recoveryRatio() >= 0.95
                && v.recentScore() >= 0.48
                && v.streakScore() >= 0.40
                && v.recencyPenalty() <= 0.05;
    }

    private double distance(ObservationVector a, ObservationVector b) {
        return Math.abs(a.activity() - b.activity())
                + Math.abs(a.urgeRatio() - b.urgeRatio())
                + Math.abs(a.attemptRatio() - b.attemptRatio())
                + Math.abs(a.blockedRatio() - b.blockedRatio())
                + Math.abs(a.recoveryRatio() - b.recoveryRatio())
                + Math.abs(a.relapseRatio() - b.relapseRatio())
                + Math.abs(a.recentScore() - b.recentScore())
                + Math.abs(a.streakScore() - b.streakScore())
                + Math.abs(a.recencyPenalty() - b.recencyPenalty());
    }

    private Map<BehaviorPattern, ObservationVector> targetVectors() {
        Map<BehaviorPattern, ObservationVector> map = new LinkedHashMap<>();

        map.put(BehaviorPattern.LOW_ACTIVITY,
                new ObservationVector(0.15, 0.00, 0.00, 0.00, 0.10, 0.00, 0.15, 0.10, 0.60));

        map.put(BehaviorPattern.QUIZ_ONLY,
                new ObservationVector(0.55, 0.00, 0.95, 0.00, 0.00, 0.00, 0.20, 0.20, 0.10));

        map.put(BehaviorPattern.WRONG_HEAVY,
                new ObservationVector(0.60, 0.50, 0.70, 0.10, 0.10, 0.80, 0.35, 0.20, 0.10));

        // 회복이 형성되는 중간 단계
        // - 위험 신호는 없음
        // - recovery 비중은 높음
        // - recent/streak는 stable보다 낮다
        // - 완전 정착 전 상태
        map.put(BehaviorPattern.RECOVERY_PROGRESS,
                new ObservationVector(0.38, 0.00, 0.00, 0.12, 0.82, 0.00, 0.36, 0.26, 0.08));

        // 회복 루틴이 안정적으로 정착된 상태
        // Day50 safe/stable dense가 이쪽으로 오도록 현실형 좌표로 조정
        map.put(BehaviorPattern.HABIT_STABLE,
                new ObservationVector(0.34, 0.00, 0.00, 0.00, 0.98, 0.00, 0.55, 0.45, 0.00));

        return map;
    }
}