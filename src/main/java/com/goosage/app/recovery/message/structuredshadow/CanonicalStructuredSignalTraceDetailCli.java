package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;
import java.util.Map;

public class CanonicalStructuredSignalTraceDetailCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        CanonicalStructuredSignalTraceShadow traceEngine =
                new CanonicalStructuredSignalTraceShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(List.of(args));

        Map<String, Object> trace =
                traceEngine.trace(events);

        System.out.println(
                "traceDetail=" + trace
        );
    }
}
