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

        map.put(BehaviorPattern.LOW_ACTIVITY,
                new ObservationVector(0.2, 0.8, 0.1, 0.0, 0.0, 0.0, 0.2, 0.1, 0.3));

        map.put(BehaviorPattern.QUIZ_ONLY,
                new ObservationVector(0.5, 0.2, 0.8, 0.0, 0.0, 0.0, 0.5, 0.4, 0.0));

        map.put(BehaviorPattern.WRONG_HEAVY,
                new ObservationVector(0.7, 0.1, 0.4, 0.1, 0.1, 0.7, 0.6, 0.4, 0.0));

        map.put(BehaviorPattern.RECOVERY_PROGRESS,
                new ObservationVector(0.7, 0.1, 0.2, 0.2, 0.6, 0.1, 0.7, 0.5, 0.0));

        map.put(BehaviorPattern.HABIT_STABLE,
                new ObservationVector(0.8, 0.2, 0.2, 0.3, 0.3, 0.0, 0.8, 0.9, 0.0));

        return map;
    }
}