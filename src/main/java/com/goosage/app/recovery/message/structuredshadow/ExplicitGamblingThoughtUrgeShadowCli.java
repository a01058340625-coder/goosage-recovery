package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class ExplicitGamblingThoughtUrgeShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        ExplicitGamblingThoughtUrgeShadow resolver =
                new ExplicitGamblingThoughtUrgeShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "explicitGamblingThoughtUrge="
                + resolver.resolve(events)
        );
    }
}
