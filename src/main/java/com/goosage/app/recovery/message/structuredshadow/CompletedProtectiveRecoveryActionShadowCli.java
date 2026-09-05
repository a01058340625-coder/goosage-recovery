package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class CompletedProtectiveRecoveryActionShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        CompletedProtectiveRecoveryActionShadow resolver =
                new CompletedProtectiveRecoveryActionShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "completedProtectiveRecoveryAction="
                + resolver.resolve(events)
        );
    }
}
