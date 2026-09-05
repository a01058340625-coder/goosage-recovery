package com.goosage.app.recovery.message.temporalshadow;

public class TemporalContextResolverShadow {

    private final TemporalRoleResolverShadow roleResolver =
            new TemporalRoleResolverShadow();

    public TemporalRoleShadow resolve(
            String eventText,
            TemporalRoleShadow previousRole
    ) {
        TemporalRoleShadow explicitRole =
                roleResolver.resolve(eventText);

        if (explicitRole != TemporalRoleShadow.UNKNOWN) {
            return explicitRole;
        }

        if (
                previousRole == TemporalRoleShadow.PAST
                || previousRole == TemporalRoleShadow.RECENT_PAST
                || previousRole == TemporalRoleShadow.CURRENT
                || previousRole == TemporalRoleShadow.HABITUAL
        ) {
            return previousRole;
        }

        return TemporalRoleShadow.UNKNOWN;
    }
}