package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class PreActionNonExecutionAttemptSuppressionShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        StringBuilder allText = new StringBuilder();

        boolean hasConcreteProgression = false;
        boolean hasSearchThought = false;

        for (StructuredEventShadow structured : events) {

            String text = structured.text();
            String action =
                    String.valueOf(
                            structured.event().actionType()
                    );
            String stage =
                    String.valueOf(
                            structured.event().actionStage()
                    );

            allText.append(text).append(" ");

            if (
                    containsAny(
                            action,
                            "SEARCH",
                            "LOGIN",
                            "FUNDING",
                            "WAGER"
                    )
                    && containsAny(
                            stage,
                            "INPUT",
                            "SUBMITTED",
                            "COMPLETED"
                    )
            ) {
                hasConcreteProgression = true;
            }

            if (
                    "SEARCH".equals(action)
                    && "THOUGHT".equals(stage)
            ) {
                hasSearchThought = true;
            }
        }

        if (hasConcreteProgression) {
            return false;
        }

        String text = allText.toString();


        // ----------------------------------------------------
        // A. Wager 직전 중단
        // ID34
        // ----------------------------------------------------

        boolean preWagerStop =
                containsAny(
                        text,
                        "돈을 걸기 직전",
                        "베팅하기 직전",
                        "베팅 직전"
                )
                && containsAny(
                        text,
                        "그냥 나왔",
                        "그냥 닫았",
                        "멈췄"
                );

        if (preWagerStop) {
            return true;
        }


        // ----------------------------------------------------
        // B. Search Thought + explicit Search/Access negation
        // ID398
        // ----------------------------------------------------

        boolean searchNegated =
                containsAny(
                        text,
                        "실제로 검색하거나",
                        "검색은 하지 않",
                        "검색하지 않"
                );

        boolean accessNegated =
                containsAny(
                        text,
                        "사이트에 들어가지는 않",
                        "사이트에 들어가지 않",
                        "접속하지 않"
                );

        if (
                hasSearchThought
                && searchNegated
                && accessNegated
        ) {
            return true;
        }

        return false;
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
