package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class PseudoProgressionAttemptSuppressionShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        PseudoProgressionAttemptSuppressionShadow resolver =
                new PseudoProgressionAttemptSuppressionShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "pseudoProgressionAttemptSuppression="
                + resolver.resolve(events)
        );
    }
}
