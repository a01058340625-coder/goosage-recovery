package com.goosage.app.recovery.message.structuredshadow;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import com.goosage.app.recovery.message.signalshadow.ShadowSignalVector;

public class StructuredSignalSequenceDiagnosticCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequenceResolver =
                new StructuredEventSequenceShadow();

        StructuredSignalMapperShadow signalMapper =
                new StructuredSignalMapperShadow();

        List<StructuredEventShadow> events =
                sequenceResolver.resolve(Arrays.asList(args));

        int index = 1;

        for (StructuredEventShadow structured : events) {

            ShadowSignalVector signal =
                    signalMapper.map(structured);

            String text64 = Base64.getEncoder().encodeToString(
                    structured.text().getBytes(StandardCharsets.UTF_8)
            );

            System.out.println(
                    "event" + index
                    + "|role=" + structured.temporalRole()
                    + "|action=" + structured.event().actionType()
                    + "|stage=" + structured.event().actionStage()
                    + "|stop=" + structured.event().stopCause()
                    + "|protective=" + structured.protectiveOutcome()
                    + "|signal="
                    + signal.urge() + ","
                    + signal.attempt() + ","
                    + signal.blocked() + ","
                    + signal.recovery() + ","
                    + signal.relapse()
                    + "|text64=" + text64
            );

            index++;
        }
    }
}
