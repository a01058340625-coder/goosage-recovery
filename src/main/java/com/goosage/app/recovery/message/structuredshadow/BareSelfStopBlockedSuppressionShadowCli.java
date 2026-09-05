package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class BareSelfStopBlockedSuppressionShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        BareSelfStopBlockedSuppressionShadow resolver =
                new BareSelfStopBlockedSuppressionShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "bareSelfStopBlockedSuppression="
                + resolver.resolve(events)
        );
    }
}
