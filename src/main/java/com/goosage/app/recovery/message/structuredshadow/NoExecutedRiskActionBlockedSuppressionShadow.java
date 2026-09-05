package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class NoExecutedRiskActionBlockedSuppressionShadow {

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
                            "ACCESS",
                            "LOGIN",
                            "FUNDING",
                            "WAGER"
                    )
                    && containsAny(
                            stage,
                            "STARTED",
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

        String text = allText.toString();


        // ----------------------------------------------------
        // A. Explicit Non-Execution
        // Parser STARTED보다 명시적 부정 Evidence 우선
        // ID168
        // ----------------------------------------------------

        boolean searchExplicitlyNegated =
                containsAny(
                        text,
                        "검색은 하지 않",
                        "검색하지 않"
                );

        boolean loginExplicitlyNegated =
                containsAny(
                        text,
                        "로그인 화면까지 간 것도 아니",
                        "로그인 화면까지 가지도 않"
                );

        boolean phonePutDown =
                containsAny(
                        text,
                        "휴대폰을 내려놨",
                        "휴대폰을 내려놓"
                );

        if (
                searchExplicitlyNegated
                && loginExplicitlyNegated
                && phonePutDown
        ) {
            return true;
        }


        // ----------------------------------------------------
        // 실제 Concrete Progression이 있으면
        // 아래 Thought-only suppression 금지
        // ----------------------------------------------------

        if (hasConcreteProgression) {
            return false;
        }


        // ----------------------------------------------------
        // B. Search Thought only
        // ID472
        // ----------------------------------------------------

        boolean thoughtOnlyStop =
                hasSearchThought
                && containsAny(
                        text,
                        "입력할까",
                        "몇 번 망설이다가"
                )
                && containsAny(
                        text,
                        "결국 휴대폰을 내려놨",
                        "결국 휴대폰을 내려놓"
                );

        if (thoughtOnlyStop) {
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
