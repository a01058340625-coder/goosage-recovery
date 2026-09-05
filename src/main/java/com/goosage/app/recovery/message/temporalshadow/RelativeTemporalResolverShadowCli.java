package com.goosage.app.recovery.message.temporalshadow;

public class RelativeTemporalResolverShadowCli {

    public static void main(String[] args) {
        RelativeTemporalResolverShadow resolver =
                new RelativeTemporalResolverShadow();

        TemporalRoleShadow previous =
                TemporalRoleShadow.UNKNOWN;

        for (String text : args) {
            TemporalRoleShadow current =
                    TemporalRoleShadow.UNKNOWN;

            TemporalRoleShadow resolvedPrevious =
                    resolver.resolvePreviousRole(
                            text,
                            previous
                    );

            TemporalRoleShadow resolvedCurrent =
                    resolver.resolveCurrentRole(
                            text,
                            current
                    );

            System.out.println(
                    "previous=" + resolvedPrevious
                    + "|current=" + resolvedCurrent
                    + "|text=" + text
            );

            previous = resolvedCurrent;
        }
    }
}