package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class ExplicitGamblingThoughtUrgeShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        boolean historical = false;

        for (StructuredEventShadow structured : events) {

            String text = structured.text();

            if (hasCurrentMarker(text)) {
                historical = false;
            }

            if (
                    hasPastOnly(text)
                    && !hasCurrentMarker(text)
            ) {
                historical = true;
            }

            if (historical) {
                continue;
            }

            if (isNegated(text)) {
                continue;
            }

            if (isExplicitGamblingThought(text)) {
                return true;
            }
        }

        return false;
    }


    private boolean isExplicitGamblingThought(String text) {
        return containsAny(
                text,
                "도박 생각은 났",
                "도박 생각이 나",
                "도박 생각이 조금 나",
                "슬롯 앱 생각이 나",
                "카지노 생각이 나",
                "베팅 생각이 나"
        );
    }


    private boolean isNegated(String text) {
        return containsAny(
                text,
                "생각도 안 났",
                "생각이 없",
                "할 생각이 없",
                "할 생각은 없"
        );
    }


    private boolean hasPastOnly(String text) {
        return containsAny(
                text,
                "예전에는",
                "몇 달 전",
                "몇 주 전",
                "몇 년 전"
        );
    }


    private boolean hasCurrentMarker(String text) {
        return containsAny(
                text,
                "요즘",
                "오늘",
                "어제",
                "최근"
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
