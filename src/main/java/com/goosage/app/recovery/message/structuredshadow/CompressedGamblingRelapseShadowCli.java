package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class CompressedGamblingRelapseShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        CompressedGamblingRelapseShadow resolver =
                new CompressedGamblingRelapseShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "compressedGamblingRelapse="
                + resolver.resolve(events)
        );
    }
}
