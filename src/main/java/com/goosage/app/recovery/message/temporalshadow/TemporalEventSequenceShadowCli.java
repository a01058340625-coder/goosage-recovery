package com.goosage.app.recovery.message.temporalshadow;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TemporalEventSequenceShadowCli {

    public static void main(String[] args) {
        TemporalBoundarySplitterShadow splitter =
                new TemporalBoundarySplitterShadow();

        TemporalContextResolverShadow contextResolver =
                new TemporalContextResolverShadow();

        TemporalRoleShadow previous =
                TemporalRoleShadow.UNKNOWN;

        int index = 1;

        for (String sentence : args) {
            for (String part : splitter.split(sentence)) {
                TemporalRoleShadow role =
                        contextResolver.resolve(part, previous);

                String encoded =
                        Base64.getEncoder().encodeToString(
                                part.getBytes(StandardCharsets.UTF_8)
                        );

                System.out.println(
                        "event" + index
                        + "|role=" + role
                        + "|text64=" + encoded
                );

                previous = role;
                index++;
            }
        }
    }
}