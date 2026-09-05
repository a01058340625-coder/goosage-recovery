package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class CanonicalDualRunShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        CanonicalStructuredSignalEngineShadow canonicalEngine =
                new CanonicalStructuredSignalEngineShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(
                        Arrays.asList(args)
                );

        /*
         * Canonical side only.
         *
         * Current Structured result is intentionally not reimplemented
         * inside this class.
         *
         * Python dual-run harness invokes:
         *
         * 1. existing StructuredSignalSequenceShadowCli
         * 2. this CanonicalDualRunShadowCli
         *
         * independently.
         *
         * This preserves the existing engine unchanged.
         */

        int[] canonical =
                canonicalEngine.resolve(events);

        System.out.println(
                "canonical="
                + canonical[0]
                + ","
                + canonical[1]
                + ","
                + canonical[2]
                + ","
                + canonical[3]
                + ","
                + canonical[4]
        );
    }
}
