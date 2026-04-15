package com.goosage.domain.recovery;

import org.springframework.stereotype.Component;

@Component
public class RecoveryStateAxisMapper {

    public RecoveryStateAxes map(RecoverySnapshot snapshot) {
        if (snapshot == null || snapshot.state() == null) {
            return RecoveryStateAxes.of(0.0, 0.0, 0.0, 0.0, 0.0);
        }

        int urge = snapshot.state().urgeLogs();
        int attempt = snapshot.state().betAttempts();
        int blocked = snapshot.state().betBlockedCount();
        int recoveryAction = snapshot.state().recoveryActionCount();
        int relapse = snapshot.state().relapseSignalCount();

        int recent3d = snapshot.recentEventCount3d();
        int streak = snapshot.streakDays();
        int daysSinceLastEvent = snapshot.daysSinceLastEvent();

        double risk = computeRisk(urge, attempt, relapse);
        double defense = computeDefense(blocked, recoveryAction);
        double recovery = computeRecovery(recoveryAction, attempt, relapse, recent3d, streak);
        double continuity = computeContinuity(recent3d, streak, daysSinceLastEvent, recoveryAction);
        double instability = computeInstability(risk, defense, recovery, continuity, urge, attempt, relapse, blocked);

        System.out.println("[AXIS-MAP] risk=" + risk
                + " defense=" + defense
                + " recovery=" + recovery
                + " continuity=" + continuity
                + " instability=" + instability);

        return RecoveryStateAxes.of(
                risk,
                defense,
                recovery,
                instability,
                continuity
        );
    }

    private double computeRisk(int urge, int attempt, int relapse) {
        double urgeScore = normalize(urge, 3);
        double attemptScore = normalize(attempt, 3);
        double relapseScore = normalize(relapse, 3);

        double weighted = (urgeScore * 0.20)
                + (attemptScore * 0.35)
                + (relapseScore * 0.45);

        return clamp(weighted);
    }

    private double computeDefense(int blocked, int recoveryAction) {
        double blockedScore = normalize(blocked, 3);
        double recoverySupport = normalize(recoveryAction, 4) * 0.25;
        return clamp(blockedScore + recoverySupport);
    }

    private double computeRecovery(int recoveryAction, int attempt, int relapse, int recent3d, int streak) {
        double base = normalize(recoveryAction, 4);

        if (recoveryAction <= 0) {
            return 0.0;
        }

        double penalty = 0.0;
        penalty += normalize(attempt, 3) * 0.35;
        penalty += normalize(relapse, 3) * 0.40;

        if (recent3d <= 1) {
            penalty += 0.15;
        }

        if (streak <= 1) {
            penalty += 0.10;
        }

        return clamp(base - penalty);
    }

    private double computeContinuity(int recent3d, int streak, int daysSinceLastEvent, int recoveryAction) {
        double recentScore = normalize(recent3d, 6);
        double streakScore = normalize(streak, 7);
        double recencyScore = 1.0 - clamp(daysSinceLastEvent / 7.0);
        double recoverySupport = recoveryAction > 0 ? 0.10 : 0.0;

        double weighted = (recentScore * 0.45)
                + (streakScore * 0.35)
                + (recencyScore * 0.20)
                + recoverySupport;

        return clamp(weighted);
    }

    private double computeInstability(
            double risk,
            double defense,
            double recovery,
            double continuity,
            int urge,
            int attempt,
            int relapse,
            int blocked
    ) {
        double mixedRiskRecovery = Math.min(risk, recovery);
        double mixedRiskDefense = Math.min(risk, defense);
        double mixedRecoveryContinuity = Math.min(recovery, continuity);

        double conflictBonus = 0.0;

        if (attempt > 0 && blocked > 0) {
            conflictBonus += 0.15;
        }

        if (relapse > 0 && recovery > 0.0) {
            conflictBonus += 0.20;
        }

        if (urge > 0 && recovery > 0.0) {
            conflictBonus += 0.10;
        }

        double weighted = (mixedRiskRecovery * 0.35)
                + (mixedRiskDefense * 0.20)
                + (mixedRecoveryContinuity * 0.25)
                + conflictBonus;

        return clamp(weighted);
    }

    private double normalize(int value, int max) {
        if (max <= 0) {
            return 0.0;
        }
        return clamp((double) value / max);
    }

    private double clamp(double value) {
        if (value < 0.0) {
            return 0.0;
        }
        if (value > 1.0) {
            return 1.0;
        }
        return value;
    }
}