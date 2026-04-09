package com.goosage.domain.predict.vector;

import org.springframework.stereotype.Component;

import com.goosage.domain.recovery.RecoverySnapshot;

@Component
public class VectorConverter {

    public ObservationVector from(RecoverySnapshot s) {
        double activity = clamp((s.state() != null ? s.state().eventsCount() : 0) / 6.0);
        double urgeRatio = s.urgeRatio();
        double attemptRatio = s.attemptRatio();
        double blockedRatio = s.blockedRatio();
        double recoveryRatio = s.recoveryRatio();
        double relapseRatio = s.relapseRatio();
        double recentScore = clamp(s.recentEventCount3d() / 12.0);
        double streakScore = clamp(s.streakDays() / 7.0);
        double recencyPenalty = clamp(s.daysSinceLastEvent() / 3.0);

        return new ObservationVector(
                activity,
                urgeRatio,
                attemptRatio,
                blockedRatio,
                recoveryRatio,
                relapseRatio,
                recentScore,
                streakScore,
                recencyPenalty
        );
    }

    private double clamp(double value) {
        if (value < 0.0) return 0.0;
        if (value > 1.0) return 1.0;
        return value;
    }
}