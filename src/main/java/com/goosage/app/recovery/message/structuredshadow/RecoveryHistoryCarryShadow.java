package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class RecoveryHistoryCarryShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        boolean historicalContext = false;

        for (StructuredEventShadow structured : events) {

            String text = structured.text();

            if (hasNewTimeBoundary(text)) {
                historicalContext = false;
            }

            if (hasHistoryStart(text)) {
                historicalContext = true;
            }

            if (!hasCompletedRecoveryEvidence(text)) {
                continue;
            }

            if (historicalContext) {
                continue;
            }

            if (hasNonActionState(text)) {
                continue;
            }

            if (hasThoughtOnly(text)) {
                continue;
            }

            return true;
        }

        return false;
    }


    private boolean hasCompletedRecoveryEvidence(
            String text
    ) {
        return containsAny(
                text,
                "앱을 삭제",
                "앱을 지웠",
                "앱도 지웠",
                "앱도 삭제",
                "휴대폰을 잠깐 맡겼",
                "휴대폰을 가족한테 맡겼",
                "결제카드를 가족에게 맡",
                "실제로 카드는 건네",
                "아내한테 얘기",
                "누나한테 먼저 얘기",
                "배우자에게 사실대로 말",
                "가족에게 말했",
                "실제로 연락한",
                "예약을 잡았",
                "상담 예약까지 잡",
                "실제로 전화해서",
                "상담 일정을 잡",
                "사이트를 차단",
                "주소를 차단"
        );
    }


    private boolean hasHistoryStart(
            String text
    ) {
        return containsAny(
                text,
                "몇 주 전",
                "몇 달 전",
                "한 달 전",
                "예전에"
        );
    }


    private boolean hasNewTimeBoundary(
            String text
    ) {
        return containsAny(
                text,
                "어제",
                "오늘",
                "다음 날",
                "다음날",
                "이번 주",
                "지난주"
        );
    }


    private boolean hasNonActionState(
            String text
    ) {
        return containsAny(
                text,
                "차단해 놓은 상태",
                "차단된 상태"
        );
    }


    private boolean hasThoughtOnly(
            String text
    ) {
        return containsAny(
                text,
                "삭제할까",
                "삭제할지",
                "고민"
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
