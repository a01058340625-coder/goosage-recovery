package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class ProtectiveDisengagementBlockedShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        ProtectiveDisengagementBlockedShadow resolver =
                new ProtectiveDisengagementBlockedShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "protectiveDisengagementBlocked="
                + resolver.resolve(events)
        );
    }
}
