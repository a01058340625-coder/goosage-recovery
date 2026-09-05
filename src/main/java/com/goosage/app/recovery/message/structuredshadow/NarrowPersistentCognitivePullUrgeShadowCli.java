package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class NarrowPersistentCognitivePullUrgeShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        NarrowPersistentCognitivePullUrgeShadow resolver =
                new NarrowPersistentCognitivePullUrgeShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "narrowPersistentCognitivePullUrge="
                + resolver.resolve(events)
        );
    }
}
