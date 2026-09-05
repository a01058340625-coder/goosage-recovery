package com.goosage.app.recovery.message.temporalshadow;

public class TemporalContextResolverShadowCli {

    public static void main(String[] args) {
        TemporalContextResolverShadow resolver =
                new TemporalContextResolverShadow();

        TemporalRoleShadow previous =
                TemporalRoleShadow.UNKNOWN;

        for (String eventText : args) {
            TemporalRoleShadow current =
                    resolver.resolve(eventText, previous);

            System.out.println(
                    "event=" + eventText
                    + " | temporalRole=" + current
            );

            previous = current;
        }
    }
}