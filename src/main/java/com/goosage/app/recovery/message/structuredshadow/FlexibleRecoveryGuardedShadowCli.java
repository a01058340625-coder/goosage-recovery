package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class FlexibleRecoveryGuardedShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        FlexibleRecoveryGuardedShadow resolver =
                new FlexibleRecoveryGuardedShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "flexibleRecoveryGuarded="
                + resolver.resolve(events)
        );
    }
}
