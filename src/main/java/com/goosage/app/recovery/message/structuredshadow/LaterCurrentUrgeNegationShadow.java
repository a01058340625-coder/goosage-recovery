package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class LaterCurrentUrgeNegationShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        for (StructuredEventShadow structured : events) {

            String text = structured.text();

            if (!hasUrgeNegation(text)) {
                continue;
            }

            if (hasCurrentMarker(text)) {
                return true;
            }

            if (
                    containsAny(
                            text,
                            "별로",
                            "거의",
                            "오히려",
                            "생기지 않",
                            "사라졌"
                    )
            ) {
                return true;
            }
        }

        return false;
    }


    private boolean hasUrgeNegation(String text) {
        return containsAny(
                text,
                "하고 싶은 마음은 안 들",
                "하고 싶은 마음이 안 들",
                "하고 싶은 마음은 없",
                "하고 싶은 마음이 없",
                "하고 싶은 마음이 거의 없",
                "하고 싶은 마음은 거의 없",
                "하고 싶은 마음이 생기지 않",
                "하고 싶은 마음이 사라졌",
                "더 하고 싶은 마음이 오히려 사라졌",
                "계속 이어가고 싶은 느낌이 없",
                "들어가고 싶은 생각이 별로 없",
                "다시 하고 싶은 마음이 들지 않",
                "다시 하고 싶은 생각이 없",
                "다시 하고 싶은 생각이 전혀 없",
                "하고 싶은 생각이 없",
                "하고 싶은 생각이 전혀 없",
                "다시 하고 싶은 생각은 들지 않",
                "다시 하고 싶은 생각은 전혀 들지 않",
                "다시 하고 싶은 마음은 없",
                "다시 하고 싶은 마음이 거의 없"
        );
    }


    private boolean hasCurrentMarker(String text) {
        return containsAny(
                text,
                "오늘",
                "지금",
                "아침에",
                "끝난 뒤",
                "그 뒤",
                "그 뒤로는",
                "종료하고 나서",
                "마지막 판이 끝난 뒤"
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
