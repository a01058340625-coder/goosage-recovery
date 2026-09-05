package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class HelpSeekingWriteRecoveryShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        HelpSeekingWriteRecoveryShadow resolver =
                new HelpSeekingWriteRecoveryShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "helpSeekingWriteRecovery="
                + resolver.resolve(events)
        );
    }
}
