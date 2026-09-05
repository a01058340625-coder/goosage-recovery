package com.goosage.app.recovery.message.temporalshadow;

public class RelativeTemporalResolverShadow {

    public boolean startsNextDay(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }

        return text.contains("다음 날")
                || text.contains("다음날");
    }

    public TemporalRoleShadow resolvePreviousRole(
            String currentText,
            TemporalRoleShadow previousRole
    ) {
        if (
                previousRole == TemporalRoleShadow.UNKNOWN
                && startsNextDay(currentText)
        ) {
            return TemporalRoleShadow.PAST;
        }

        return previousRole;
    }

    public TemporalRoleShadow resolveCurrentRole(
            String currentText,
            TemporalRoleShadow currentRole
    ) {
        if (
                currentRole == TemporalRoleShadow.UNKNOWN
                && startsNextDay(currentText)
        ) {
            return TemporalRoleShadow.RECENT_PAST;
        }

        return currentRole;
    }
}