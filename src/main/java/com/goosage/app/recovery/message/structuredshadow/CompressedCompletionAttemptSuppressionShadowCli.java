package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class CompressedCompletionAttemptSuppressionShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        CompressedCompletionAttemptSuppressionShadow resolver =
                new CompressedCompletionAttemptSuppressionShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "compressedCompletionAttemptSuppression="
                + resolver.resolve(events)
        );
    }
}
