package com.goosage.domain.recovery;

public record RecoveryStateAxes(
        double risk,
        double defense,
        double recovery,
        double instability,
        double continuity
) {

    public RecoveryStateAxes {
        risk = clamp(risk);
        defense = clamp(defense);
        recovery = clamp(recovery);
        instability = clamp(instability);
        continuity = clamp(continuity);
    }

    public static RecoveryStateAxes of(
            double risk,
            double defense,
            double recovery,
            double instability,
            double continuity
    ) {
        return new RecoveryStateAxes(risk, defense, recovery, instability, continuity);
    }

    public boolean isRiskDominant() {
        return risk >= 0.6
                && risk >= defense
                && risk >= recovery
                && risk >= continuity
                && instability < 0.5;
    }

    public boolean isRecoveryDominant() {
        return recovery >= 0.6
                && recovery >= risk
                && recovery >= defense
                && recovery >= continuity
                && instability < 0.5;
    }

    public boolean isDefenseDominant() {
        return defense >= 0.5
                && defense >= risk
                && defense >= recovery
                && defense >= continuity
                && instability < 0.5;
    }

    public boolean isUnstable() {
        return instability >= 0.5;
    }

    public boolean isLowContinuity() {
        return continuity < 0.35;
    }

    public boolean isStableContinuity() {
        return continuity >= 0.6
                && recovery < 0.6
                && instability < 0.5;
    }

    private static double clamp(double value) {
        if (value < 0.0) {
            return 0.0;
        }
        if (value > 1.0) {
            return 1.0;
        }
        return value;
    }
}