package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class ConcreteWagerExecutionAttemptShadow {

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

            if (hasConcreteWagerExecution(text)) {
                return true;
            }
        }

        return false;
    }


    private boolean hasConcreteWagerExecution(
            String text
    ) {
        return containsAny(
                text,
                "슬롯을 돌렸",
                "슬롯을 계속 돌",
                "몇 번 베팅했",
                "몇 번 베팅했고",
                "몇 판 했",
                "몇 판 더 했",
                "실제로 베팅을 한"
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
