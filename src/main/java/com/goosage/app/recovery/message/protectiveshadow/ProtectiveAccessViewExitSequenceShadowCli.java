package com.goosage.app.recovery.message.protectiveshadow;

import java.util.Arrays;
import java.util.List;

import com.goosage.app.recovery.message.structuredshadow.StructuredEventSequenceShadow;
import com.goosage.app.recovery.message.structuredshadow.StructuredEventShadow;

public class ProtectiveAccessViewExitSequenceShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        ProtectiveAccessViewExitSequenceShadow resolver =
                new ProtectiveAccessViewExitSequenceShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "protectiveAccessViewExit="
                + resolver.resolve(events)
        );
    }
}
