package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class StrongDesireTemporalGuardedUrgeShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        StrongDesireTemporalGuardedUrgeShadow resolver =
                new StrongDesireTemporalGuardedUrgeShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "strongDesireTemporalGuardedUrge="
                + resolver.resolve(events)
        );
    }
}
