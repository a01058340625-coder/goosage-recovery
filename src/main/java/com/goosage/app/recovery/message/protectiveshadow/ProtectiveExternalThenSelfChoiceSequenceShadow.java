package com.goosage.app.recovery.message.protectiveshadow;

import java.util.List;

import com.goosage.app.recovery.message.structuredshadow.StructuredEventShadow;

public class ProtectiveExternalThenSelfChoiceSequenceShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {
        boolean externalSeen = false;

        for (StructuredEventShadow structured : events) {

            String text = structured.text();

            if (isExternal(text)) {
                externalSeen = true;
                continue;
            }

            if (
                    externalSeen
                    && isSelfChoice(text)
            ) {
                return true;
            }
        }

        return false;
    }

    private boolean isExternal(String text) {
        return containsAny(
                text,
                "전화",
                "통화",
                "가족이",
                "저녁 먹으라고"
        );
    }

    private boolean isSelfChoice(String text) {
        return containsAny(
                text,
                "다시 하려다가 그냥",
                "다시 하지 않",
                "아무것도 안 하",
                "사이트를 닫",
                "컴퓨터를 끄",
                "더 하고 싶은 마음이 많이 줄"
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
