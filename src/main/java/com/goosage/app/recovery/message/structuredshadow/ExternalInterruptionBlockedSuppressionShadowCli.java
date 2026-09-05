package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class ExternalInterruptionBlockedSuppressionShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        ExternalInterruptionBlockedSuppressionShadow resolver =
                new ExternalInterruptionBlockedSuppressionShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "externalInterruptionBlockedSuppression="
                + resolver.resolve(events)
        );
    }
}
