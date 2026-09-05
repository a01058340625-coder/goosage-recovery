package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class OngoingGamblingBehaviorRelapseShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        StringBuilder allText = new StringBuilder();

        for (StructuredEventShadow structured : events) {
            allText.append(structured.text()).append(" ");
        }

        String text = allText.toString();


        // ----------------------------------------------------
        // A. Ongoing compulsive gambling behavior
        // ID125
        // ----------------------------------------------------

        boolean ongoingCompulsiveBehavior =
                containsAny(
                        text,
                        "하루 종일 결과 확인",
                        "일하다가도 몰래 보고"
                )
                && containsAny(
                        text,
                        "돈도 꽤 많이 들어갔",
                        "사이트를 켜게"
                );

        if (ongoingCompulsiveBehavior) {
            return true;
        }


        // ----------------------------------------------------
        // B. Habitual betting continuation
        // ID134
        // ----------------------------------------------------

        boolean habitualBettingBehavior =
                containsAny(
                        text,
                        "하루라도 안 하면 계속 생각",
                        "생활의 일부가 된"
                )
                && containsAny(
                        text,
                        "경기 있으면 자연스럽게 배팅할 곳부터 찾",
                        "배팅할 곳부터 찾"
                );

        if (habitualBettingBehavior) {
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
