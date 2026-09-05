package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class NonGamblingDesireTargetUrgeSuppressionShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        NonGamblingDesireTargetUrgeSuppressionShadow resolver =
                new NonGamblingDesireTargetUrgeSuppressionShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "nonGamblingDesireTargetUrgeSuppression="
                + resolver.resolve(events)
        );
    }
}
