package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class RecoveryHistoryCarryShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        RecoveryHistoryCarryShadow resolver =
                new RecoveryHistoryCarryShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "recoveryHistoryCarry="
                + resolver.resolve(events)
        );
    }
}
