package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class TriggeredCompulsivePullUrgeShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        TriggeredCompulsivePullUrgeShadow resolver =
                new TriggeredCompulsivePullUrgeShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "triggeredCompulsivePullUrge="
                + resolver.resolve(events)
        );
    }
}
