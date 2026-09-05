package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class WagerCompletionRelapseShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        WagerCompletionRelapseShadow resolver =
                new WagerCompletionRelapseShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "wagerCompletionRelapse="
                + resolver.resolve(events)
        );
    }
}
