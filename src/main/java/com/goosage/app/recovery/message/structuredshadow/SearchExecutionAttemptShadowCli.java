package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class SearchExecutionAttemptShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        SearchExecutionAttemptShadow resolver =
                new SearchExecutionAttemptShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "searchExecutionAttempt="
                + resolver.resolve(events)
        );
    }
}
