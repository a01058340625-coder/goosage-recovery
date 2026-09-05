package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class HabitualNarrativeAttemptSuppressionShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        boolean hasWagerStarted = false;
        StringBuilder allText = new StringBuilder();

        for (StructuredEventShadow structured : events) {

            allText.append(structured.text()).append(" ");

            if (
                    "WAGER".equals(
                            String.valueOf(
                                    structured.event().actionType()
                            )
                    )
                    && "STARTED".equals(
                            String.valueOf(
                                    structured.event().actionStage()
                            )
                    )
            ) {
                hasWagerStarted = true;
            }
        }

        if (!hasWagerStarted) {
            return false;
        }

        String text = allText.toString();

        return containsAny(
                text,
                "베팅하게 돼",
                "베팅을 시작하면",
                "다시 하게 돼",
                "돈만 생기면",
                "계속 반복"
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
