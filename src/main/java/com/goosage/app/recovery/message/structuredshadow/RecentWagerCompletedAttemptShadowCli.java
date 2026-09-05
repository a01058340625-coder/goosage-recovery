package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class RecentWagerCompletedAttemptShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        RecentWagerCompletedAttemptShadow resolver =
                new RecentWagerCompletedAttemptShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "recentWagerCompletedAttempt="
                + resolver.resolve(events)
        );
    }
}
