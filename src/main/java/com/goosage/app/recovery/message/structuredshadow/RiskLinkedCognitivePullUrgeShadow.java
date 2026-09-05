package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class RiskLinkedCognitivePullUrgeShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        StringBuilder allText = new StringBuilder();

        for (StructuredEventShadow structured : events) {
            allText.append(structured.text()).append(" ");
        }

        String text = allText.toString();


        // ----------------------------------------------------
        // A. Explicit cognitive pull / destabilization
        // ----------------------------------------------------

        if (
                containsAny(
                        text,
                        "잠깐 흔들렸",
                        "자주 떠오릅",
                        "자꾸 떠오른",
                        "신경이 쓰이",
                        "비슷한 기분이 올라왔",
                        "이상하게 또렷"
                )
        ) {
            return true;
        }


        // ----------------------------------------------------
        // B. Loss-linked recall
        // ----------------------------------------------------

        if (
                containsAny(
                        text,
                        "예전에 잃은 돈 생각",
                        "예전에 잃었던 돈이 생각",
                        "본전 생각"
                )
        ) {
            return true;
        }


        // ----------------------------------------------------
        // C. Recall -> gambling-oriented action thought
        // ----------------------------------------------------

        boolean gamblingRecall =
                containsAny(
                        text,
                        "사이트 이름이 떠올랐",
                        "사이트가 떠올랐",
                        "그 말이 다시 떠올랐",
                        "그때가 생각났"
                );

        boolean actionDirection =
                containsAny(
                        text,
                        "찾아보려다가",
                        "검색창",
                        "검색",
                        "사이트",
                        "앱을 찾"
                );

        if (
                gamblingRecall
                && actionDirection
        ) {
            return true;
        }


        // ----------------------------------------------------
        // D. Persistent loss cognition
        // ----------------------------------------------------

        boolean lossThought =
                containsAny(
                        text,
                        "잃었던 돈이 생각",
                        "잃은 돈이 생각"
                );

        boolean persistent =
                containsAny(
                        text,
                        "계속 떠올랐",
                        "숫자가 계속"
                );

        if (
                lossThought
                && persistent
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
