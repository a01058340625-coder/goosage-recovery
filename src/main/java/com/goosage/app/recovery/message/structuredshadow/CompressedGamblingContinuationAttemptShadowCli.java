package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class CompressedGamblingContinuationAttemptShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        CompressedGamblingContinuationAttemptShadow resolver =
                new CompressedGamblingContinuationAttemptShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "compressedGamblingContinuationAttempt="
                + resolver.resolve(events)
        );
    }
}
