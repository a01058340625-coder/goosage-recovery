package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class CanonicalStructuredSignalEngineShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        CanonicalStructuredSignalEngineShadow engine =
                new CanonicalStructuredSignalEngineShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        int[] signal =
                engine.resolve(events);

        System.out.println(
                "canonical="
                + signal[0]
                + ","
                + signal[1]
                + ","
                + signal[2]
                + ","
                + signal[3]
                + ","
                + signal[4]
        );
    }
}
