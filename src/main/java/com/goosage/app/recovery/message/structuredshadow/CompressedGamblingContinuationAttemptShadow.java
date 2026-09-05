package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class CompressedGamblingContinuationAttemptShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        for (StructuredEventShadow structured : events) {

            String stage =
                    structured.event().actionStage().name();

            String text =
                    structured.text();

            if (stage.equals("THOUGHT")) {
                continue;
            }

            if (hasExecutionEvidence(text)) {
                return true;
            }
        }

        return false;
    }


    private boolean hasExecutionEvidence(
            String text
    ) {
        return containsAny(
                text,
                "결국 다시 들어가게 됐",
                "도박을 하고 있는 상태",
                "결국 다시 했",
                "도박을 하게 됐",
                "계속 하게 됐"
        );
    }


    private boolean containsAny(
            String text,
            String... values
    ) {
        for (String value : values) {
            if (text.contains(value)) {
                return true;
            }
        }

        return false;
    }
}
