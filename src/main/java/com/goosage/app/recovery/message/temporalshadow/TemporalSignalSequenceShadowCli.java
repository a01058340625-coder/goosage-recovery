package com.goosage.app.recovery.message.temporalshadow;

import com.goosage.app.recovery.message.signalshadow.ShadowSignalVector;
import com.goosage.app.recovery.message.urgeshadow.UrgeResolverShadow;

public class TemporalSignalSequenceShadowCli {

    public static void main(String[] args) {

        TemporalBoundarySplitterShadow splitter =
                new TemporalBoundarySplitterShadow();

        TemporalEventDescriptorResolverShadow resolver =
                new TemporalEventDescriptorResolverShadow();

        TemporalSignalMapperShadow mapper =
                new TemporalSignalMapperShadow();

        UrgeResolverShadow urgeResolver =
                new UrgeResolverShadow();

        TemporalRoleShadow previous =
                TemporalRoleShadow.UNKNOWN;

        int urge = 0;
        int attempt = 0;
        int blocked = 0;
        int recovery = 0;
        int relapse = 0;

        int index = 1;

        for (String sentence : args) {
            for (String part : splitter.split(sentence)) {

                TemporalEventDescriptorShadow event =
                        resolver.resolve(part, previous);

                ShadowSignalVector baseSignal =
                        mapper.map(event);

                int eventUrge = 0;

                if (
                        event.temporalRole() == TemporalRoleShadow.CURRENT
                        || event.temporalRole() == TemporalRoleShadow.RECENT_PAST
                ) {
                    eventUrge =
                            urgeResolver.resolve(part).urge();
                }

                ShadowSignalVector signal =
                        new ShadowSignalVector(
                                eventUrge,
                                baseSignal.attempt(),
                                baseSignal.blocked(),
                                baseSignal.recovery(),
                                baseSignal.relapse()
                        );

                System.out.println(
                        "event" + index
                        + "|role=" + event.temporalRole()
                        + "|action=" + event.event().actionType()
                        + "|stage=" + event.event().actionStage()
                        + "|stop=" + event.event().stopCause()
                        + "|signal="
                        + signal.urge() + ","
                        + signal.attempt() + ","
                        + signal.blocked() + ","
                        + signal.recovery() + ","
                        + signal.relapse()
                );

                urge = Math.max(urge, signal.urge());
                attempt = Math.max(attempt, signal.attempt());
                blocked = Math.max(blocked, signal.blocked());
                recovery = Math.max(recovery, signal.recovery());
                relapse = Math.max(relapse, signal.relapse());

                previous = event.temporalRole();
                index++;
            }
        }

        System.out.println(
                "aggregate="
                + urge + ","
                + attempt + ","
                + blocked + ","
                + recovery + ","
                + relapse
        );
    }
}