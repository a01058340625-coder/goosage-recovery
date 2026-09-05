package com.goosage.app.recovery.message.temporalshadow;

import java.util.ArrayList;
import java.util.List;

public class TemporalSequenceRelationShadow {

    private final TemporalRoleResolverShadow roleResolver =
            new TemporalRoleResolverShadow();

    private final RelativeTemporalResolverShadow relativeResolver =
            new RelativeTemporalResolverShadow();

    public List<TemporalRoleShadow> resolve(
            List<String> events
    ) {
        List<TemporalRoleShadow> roles =
                new ArrayList<>();

        TemporalRoleShadow previous =
                TemporalRoleShadow.UNKNOWN;

        for (int i = 0; i < events.size(); i++) {
            String text = events.get(i);

            TemporalRoleShadow current =
                    roleResolver.resolve(text);

            if (
                    i > 0
                    && relativeResolver.startsNextDay(text)
                    && roles.get(i - 1) == TemporalRoleShadow.UNKNOWN
            ) {
                roles.set(
                        i - 1,
                        TemporalRoleShadow.PAST
                );
            }

            if (
                    current == TemporalRoleShadow.UNKNOWN
                    && relativeResolver.startsNextDay(text)
            ) {
                current =
                        TemporalRoleShadow.RECENT_PAST;
            }

            if (
                    current == TemporalRoleShadow.UNKNOWN
                    && previous != TemporalRoleShadow.UNKNOWN
            ) {
                current = previous;
            }

            roles.add(current);
            previous = current;
        }

        return roles;
    }
}