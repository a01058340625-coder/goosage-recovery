package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;

public class CurrentStructuredSignalEngineShadowCli {

    public static void main(String[] args) {

        CurrentStructuredSignalEngineShadow engine =
                new CurrentStructuredSignalEngineShadow();

        int[] signal =
                engine.resolve(
                        Arrays.asList(args)
                );

        System.out.println(
                "current="
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
