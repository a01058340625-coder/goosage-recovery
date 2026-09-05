package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class HabitualNarrativeAttemptSuppressionShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        HabitualNarrativeAttemptSuppressionShadow resolver =
                new HabitualNarrativeAttemptSuppressionShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "habitualNarrativeAttemptSuppression="
                + resolver.resolve(events)
        );
    }
}
