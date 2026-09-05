package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class ConcreteWagerExecutionAttemptShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        ConcreteWagerExecutionAttemptShadow resolver =
                new ConcreteWagerExecutionAttemptShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "concreteWagerExecutionAttempt="
                + resolver.resolve(events)
        );
    }
}
