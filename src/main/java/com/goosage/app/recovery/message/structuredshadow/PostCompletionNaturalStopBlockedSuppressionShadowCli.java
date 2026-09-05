package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class PostCompletionNaturalStopBlockedSuppressionShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        PostCompletionNaturalStopBlockedSuppressionShadow resolver =
                new PostCompletionNaturalStopBlockedSuppressionShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "postCompletionNaturalStopBlockedSuppression="
                + resolver.resolve(events)
        );
    }
}
