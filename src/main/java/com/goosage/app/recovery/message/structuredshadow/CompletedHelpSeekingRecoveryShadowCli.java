package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class CompletedHelpSeekingRecoveryShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        CompletedHelpSeekingRecoveryShadow shadow =
                new CompletedHelpSeekingRecoveryShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(
                        Arrays.asList(args)
                );

        boolean matched =
                shadow.resolve(
                        events
                );

        System.out.println(
                "completedHelpSeekingRecovery="
                + matched
        );
    }
}
