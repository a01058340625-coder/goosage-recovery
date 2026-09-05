package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class PartialSearchInputAttemptShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        for (int i = 0; i < events.size(); i++) {

            StructuredEventShadow current =
                    events.get(i);

            String text =
                    current.text();

            if (hasNoInputEvidence(text)) {
                continue;
            }

            if (hasPartialInputEvidence(text)) {
                return true;
            }

            if (
                    text.contains("검색창을 열")
                    && i + 1 < events.size()
            ) {

                StructuredEventShadow next =
                        events.get(i + 1);

                String nextAction =
                        next.event().actionType().name();

                String nextStage =
                        next.event().actionStage().name();

                String nextText =
                        next.text();

                if (
                        nextAction.equals("SEARCH")
                        && !nextStage.equals("THOUGHT")
                        && !hasNoInputEvidence(nextText)
                        && hasPartialInputEvidence(nextText)
                ) {
                    return true;
                }
            }
        }

        return false;
    }


    private boolean hasPartialInputEvidence(
            String text
    ) {
        return containsAny(
                text,
                "몇 글자",
                "끝까지 안 쳤"
        );
    }


    private boolean hasNoInputEvidence(
            String text
    ) {
        return containsAny(
                text,
                "아무것도 입력하지 않",
                "입력하지 않고"
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
