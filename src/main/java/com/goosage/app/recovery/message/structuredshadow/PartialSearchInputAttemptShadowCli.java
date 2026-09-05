package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class PartialSearchInputAttemptShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        PartialSearchInputAttemptShadow resolver =
                new PartialSearchInputAttemptShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "partialSearchInputAttempt="
                + resolver.resolve(events)
        );
    }
}
