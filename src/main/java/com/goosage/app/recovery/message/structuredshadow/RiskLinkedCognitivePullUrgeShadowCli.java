package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class RiskLinkedCognitivePullUrgeShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        RiskLinkedCognitivePullUrgeShadow resolver =
                new RiskLinkedCognitivePullUrgeShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "riskLinkedCognitivePullUrge="
                + resolver.resolve(events)
        );
    }
}
