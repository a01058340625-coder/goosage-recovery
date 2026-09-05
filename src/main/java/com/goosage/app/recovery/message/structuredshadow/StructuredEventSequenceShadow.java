package com.goosage.app.recovery.message.structuredshadow;

import java.util.ArrayList;
import java.util.List;

import com.goosage.app.recovery.message.actionshadow.ActionBoundarySplitterShadow;
import com.goosage.app.recovery.message.event.EventDescriptor;
import com.goosage.app.recovery.message.event.EventDescriptorResolverShadow;
import com.goosage.app.recovery.message.protectiveshadow.ProtectiveOutcomeShadow;
import com.goosage.app.recovery.message.stopcause.StopCauseType;
import com.goosage.app.recovery.message.temporalshadow.RelativeTemporalResolverShadow;
import com.goosage.app.recovery.message.temporalshadow.TemporalBoundarySplitterShadow;
import com.goosage.app.recovery.message.temporalshadow.TemporalRoleResolverShadow;
import com.goosage.app.recovery.message.temporalshadow.TemporalRoleShadow;

public class StructuredEventSequenceShadow {

    private final TemporalBoundarySplitterShadow temporalSplitter =
            new TemporalBoundarySplitterShadow();

    private final ActionBoundarySplitterShadow actionSplitter =
            new ActionBoundarySplitterShadow();

    private final TemporalRoleResolverShadow temporalRoleResolver =
            new TemporalRoleResolverShadow();

    private final RelativeTemporalResolverShadow relativeTemporalResolver =
            new RelativeTemporalResolverShadow();

    private final EventDescriptorResolverShadow eventResolver =
            new EventDescriptorResolverShadow();

    private final ProtectiveOutcomeShadow protectiveResolver =
            new ProtectiveOutcomeShadow();

    public List<StructuredEventShadow> resolve(
            List<String> sentences
    ) {
        List<String> temporalParts = new ArrayList<>();

        for (String sentence : sentences) {
            temporalParts.addAll(
                    temporalSplitter.split(sentence)
            );
        }

        List<TemporalRoleShadow> temporalRoles =
                resolveTemporalRoles(temporalParts);

        List<StructuredEventShadow> result =
                new ArrayList<>();

        for (int i = 0; i < temporalParts.size(); i++) {
            String temporalPart = temporalParts.get(i);
            TemporalRoleShadow role = temporalRoles.get(i);

            for (String actionPart : actionSplitter.split(temporalPart)) {
                EventDescriptor event =
                        eventResolver.resolve(actionPart);

                boolean existingSelfStop =
                        event.stopCause() == StopCauseType.SELF_STOP;

                boolean protective =
                        protectiveResolver.resolve(
                                actionPart,
                                existingSelfStop
                        );

                result.add(
                        new StructuredEventShadow(
                                actionPart,
                                role,
                                event,
                                protective
                        )
                );
            }
        }

        return result;
    }

    private List<TemporalRoleShadow> resolveTemporalRoles(
            List<String> events
    ) {
        List<TemporalRoleShadow> roles =
                new ArrayList<>();

        TemporalRoleShadow previous =
                TemporalRoleShadow.UNKNOWN;

        for (int i = 0; i < events.size(); i++) {
            String text = events.get(i);

            TemporalRoleShadow current =
                    temporalRoleResolver.resolve(text);

            if (
                    i > 0
                    && relativeTemporalResolver.startsNextDay(text)
                    && roles.get(i - 1) == TemporalRoleShadow.UNKNOWN
            ) {
                roles.set(
                        i - 1,
                        TemporalRoleShadow.PAST
                );
            }

            if (
                    current == TemporalRoleShadow.UNKNOWN
                    && relativeTemporalResolver.startsNextDay(text)
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