package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class SearchExecutionAttemptShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        for (StructuredEventShadow structured : events) {

            String action =
                    structured.event().actionType().name();

            String stage =
                    structured.event().actionStage().name();

            String text =
                    structured.text();

            if (!action.equals("SEARCH")) {
                continue;
            }

            if (stage.equals("THOUGHT")) {
                continue;
            }

            if (hasSearchExecutionEvidence(text)) {
                return true;
            }
        }

        return false;
    }


    private boolean hasSearchExecutionEvidence(
            String text
    ) {
        return containsAny(
                text,
                "검색해봤",
                "검색해 봤",
                "검색해본",
                "검색해 본",
                "검색해서",
                "검색해 보니",
                "검색창에",
                "몇 글자",
                "검색 결과"
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
