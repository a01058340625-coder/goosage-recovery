package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class LaterCurrentUrgeNegationShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        LaterCurrentUrgeNegationShadow resolver =
                new LaterCurrentUrgeNegationShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "laterCurrentUrgeNegation="
                + resolver.resolve(events)
        );
    }
}
