package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class HelpSeekingWriteRecoveryShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        boolean historical = false;

        for (StructuredEventShadow structured : events) {

            String text = structured.text();

            if (hasNewTime(text)) {
                historical = false;
            }

            if (hasHistoryStart(text)) {
                historical = true;
            }

            if (hasCurrentIntentOnly(text)) {
                continue;
            }

            if (historical) {
                continue;
            }

            if (hasHelpWrite(text)) {
                return true;
            }
        }

        return false;
    }


    private boolean hasHelpWrite(String text) {
        return containsAny(
                text,
                "글 남깁니다",
                "글을 남깁니다",
                "여기까지 왔습니다",
                "직접 글을 남겼습니다"
        );
    }


    private boolean hasHistoryStart(String text) {
        return containsAny(
                text,
                "지난달",
                "몇 달 전",
                "몇 주 전",
                "예전에"
        );
    }


    private boolean hasNewTime(String text) {
        return containsAny(
                text,
                "이번 주",
                "이번 주말",
                "오늘",
                "어제",
                "지금"
        );
    }


    private boolean hasCurrentIntentOnly(String text) {
        return containsAny(
                text,
                "다시 연락하려고",
                "연락하려고"
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
