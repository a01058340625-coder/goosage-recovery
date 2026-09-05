package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class PreActionNonExecutionAttemptSuppressionShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        PreActionNonExecutionAttemptSuppressionShadow resolver =
                new PreActionNonExecutionAttemptSuppressionShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "preActionNonExecutionAttemptSuppression="
                + resolver.resolve(events)
        );
    }
}
