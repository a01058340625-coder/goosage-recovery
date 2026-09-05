package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class NoExecutedRiskActionBlockedSuppressionShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        NoExecutedRiskActionBlockedSuppressionShadow resolver =
                new NoExecutedRiskActionBlockedSuppressionShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "noExecutedRiskActionBlockedSuppression="
                + resolver.resolve(events)
        );
    }
}
