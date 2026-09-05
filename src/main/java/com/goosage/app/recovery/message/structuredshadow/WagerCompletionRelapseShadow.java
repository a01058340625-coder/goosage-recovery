package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class WagerCompletionRelapseShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        for (StructuredEventShadow structured : events) {

            String stage =
                    structured.event().actionStage().name();

            String text =
                    structured.text();

            if (stage.equals("THOUGHT")) {
                continue;
            }

            if (hasCompletionEvidence(text)) {
                return true;
            }
        }

        return false;
    }


    private boolean hasCompletionEvidence(
            String text
    ) {
        return containsAny(
                text,
                "베팅을 했습니다",
                "베팅을 한 뒤",
                "한 번 걸었",
                "베팅을 제출",
                "첫 베팅은 실패",
                "슬롯을 한 번 돌렸",
                "슬롯을 몇 판",
                "슬롯을 여러 번 돌렸",
                "몇 번 베팅했",
                "몇 판 했",
                "몇 판 더 했",
                "결과가 나온 뒤",
                "적중하지 않",
                "끝나고 나니까",
                "몇 번은 따기도",
                "수익이 나",
                "손실이 커졌",
                "몇 판을 돌렸",
                "두세 번 더 하게",
                "마지막 판이 끝난"
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
