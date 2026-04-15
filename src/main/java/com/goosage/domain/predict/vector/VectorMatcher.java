package com.goosage.domain.predict.vector;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class VectorMatcher {

    public BehaviorPattern match(ObservationVector v) {
        Map<BehaviorPattern, ObservationVector> targets = targetVectors();

        BehaviorPattern best = null;
        double bestDistance = Double.MAX_VALUE;

        for (Map.Entry<BehaviorPattern, ObservationVector> entry : targets.entrySet()) {
            double d = distance(v, entry.getValue());
            if (d < bestDistance) {
                bestDistance = d;
                best = entry.getKey();
            }
        }

        return best;
    }

    public double distanceTo(ObservationVector actual, BehaviorPattern pattern) {
        return distance(actual, targetVectors().get(pattern));
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

        // 저활동: urge를 높게 두지 말고, 전체 활동 저하 + 최근성 약화 중심
        map.put(BehaviorPattern.LOW_ACTIVITY,
                new ObservationVector(0.15, 0.0, 0.0, 0.0, 0.1, 0.0, 0.15, 0.10, 0.60));

        // recovery 도메인에서는 사실상 노이즈에 가까움 → 가장 덜 끌리게 약화
        map.put(BehaviorPattern.QUIZ_ONLY,
                new ObservationVector(0.55, 0.0, 0.95, 0.0, 0.0, 0.0, 0.20, 0.20, 0.10));

        // 위험 신호: urge/attempt/relapse 중심
        map.put(BehaviorPattern.WRONG_HEAVY,
                new ObservationVector(0.60, 0.50, 0.70, 0.10, 0.10, 0.80, 0.35, 0.20, 0.10));

        // 회복 진행: recovery + blocked + recent/streak 가 살아있어야 함
        map.put(BehaviorPattern.RECOVERY_PROGRESS,
                new ObservationVector(0.45, 0.00, 0.00, 0.35, 0.75, 0.00, 0.45, 0.35, 0.20));

        map.put(BehaviorPattern.HABIT_STABLE,
                new ObservationVector(0.80, 0.00, 0.00, 0.20, 0.40, 0.00, 0.80, 0.90, 0.00));

        return map;
    }
}