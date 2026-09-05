package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class OngoingGamblingBehaviorRelapseShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        OngoingGamblingBehaviorRelapseShadow resolver =
                new OngoingGamblingBehaviorRelapseShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "ongoingGamblingBehaviorRelapse="
                + resolver.resolve(events)
        );
    }
}
