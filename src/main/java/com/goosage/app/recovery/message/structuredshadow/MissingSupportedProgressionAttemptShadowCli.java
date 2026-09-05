package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class MissingSupportedProgressionAttemptShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        MissingSupportedProgressionAttemptShadow resolver =
                new MissingSupportedProgressionAttemptShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "missingSupportedProgressionAttempt="
                + resolver.resolve(events)
        );
    }
}
