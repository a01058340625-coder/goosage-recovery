package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class RecentWagerCompletedAttemptShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        boolean recentContext = false;

        /*
         * Temporal은 Global Gate가 아니라
         * Relevant Event를 판단하기 위한 Context Evidence로만 사용.
         */
        for (StructuredEventShadow structured : events) {

            String text = structured.text();

            if (
                    text.contains("\uc9c0\ub09c\uc8fc")
                    || text.contains("\uc9c0\ub09c \uc8fc")
                    || text.contains("\uc5b4\uc81c")
                    || text.contains("\uc624\ub298")
                    || text.contains("\uc774\ubc88 \uc8fc")
                    || text.contains("\ucd5c\uadfc")
                    || text.contains("\uc9c0\ub09c\ub2ec")
            ) {
                recentContext = true;
            }
        }

        if (!recentContext) {
            return false;
        }


        for (StructuredEventShadow structured : events) {

            String action =
                    structured.event().actionType().name();

            String stage =
                    structured.event().actionStage().name();

            boolean protective =
                    structured.protectiveOutcome();

            if (
                    action.equals("WAGER")
                    && stage.equals("COMPLETED")
                    && !protective
            ) {
                return true;
            }
        }

        return false;
    }
}
