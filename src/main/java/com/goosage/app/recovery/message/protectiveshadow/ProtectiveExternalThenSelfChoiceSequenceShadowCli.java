package com.goosage.app.recovery.message.protectiveshadow;

import java.util.Arrays;
import java.util.List;

import com.goosage.app.recovery.message.structuredshadow.StructuredEventSequenceShadow;
import com.goosage.app.recovery.message.structuredshadow.StructuredEventShadow;

public class ProtectiveExternalThenSelfChoiceSequenceShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        ProtectiveExternalThenSelfChoiceSequenceShadow resolver =
                new ProtectiveExternalThenSelfChoiceSequenceShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "protectiveExternalThenSelfChoice="
                + resolver.resolve(events)
        );
    }
}
