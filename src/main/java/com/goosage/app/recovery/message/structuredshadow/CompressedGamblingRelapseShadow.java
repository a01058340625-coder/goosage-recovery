package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class CompressedGamblingRelapseShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        for (StructuredEventShadow structured : events) {

            String text =
                    structured.text();

            if (hasRelapseEvidence(text)) {
                return true;
            }
        }

        return false;
    }


    private boolean hasRelapseEvidence(
            String text
    ) {
        return containsAny(
                text,
                "또 베팅하게 돼",
                "다시 도박을 시작하게 돼",
                "도박을 시작했어",
                "손을 대기 시작했",
                "다시 시작했다가",
                "도박을 하고 있는 상태",
                "또 들어가게 되",
                "같이 하게 됐",
                "결국 다시 했",
                "도박을 하게 됐",
                "모바일 카지노를 시작",
                "계속 하게 됐",
                "베팅을 다시 시작",
                "다시 하게 돼"
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
