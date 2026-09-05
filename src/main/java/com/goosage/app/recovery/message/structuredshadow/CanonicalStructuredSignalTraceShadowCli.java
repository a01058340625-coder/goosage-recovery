package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;
import java.util.Map;

public class CanonicalStructuredSignalTraceShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        CanonicalStructuredSignalTraceShadow traceEngine =
                new CanonicalStructuredSignalTraceShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(List.of(args));

        Map<String, Object> trace =
                traceEngine.trace(events);

        @SuppressWarnings("unchecked")
        Map<String, Integer> signal =
                (Map<String, Integer>)
                        trace.get("finalSignalVector");

        System.out.println(
                "trace="
                + signal.get("urge")
                + ","
                + signal.get("attempt")
                + ","
                + signal.get("blocked")
                + ","
                + signal.get("recovery")
                + ","
                + signal.get("relapse")
        );
    }
}
