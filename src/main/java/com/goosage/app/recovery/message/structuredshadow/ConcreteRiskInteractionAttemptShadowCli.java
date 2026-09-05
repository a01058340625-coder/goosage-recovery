package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class ConcreteRiskInteractionAttemptShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        ConcreteRiskInteractionAttemptShadow resolver =
                new ConcreteRiskInteractionAttemptShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "concreteRiskInteractionAttempt="
                + resolver.resolve(events)
        );
    }
}
