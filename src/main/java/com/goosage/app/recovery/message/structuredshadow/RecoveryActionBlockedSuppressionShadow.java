package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class RecoveryActionBlockedSuppressionShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        for (StructuredEventShadow structured : events) {

            String text = structured.text();

            if (text == null || text.isBlank()) {
                continue;
            }

            if (
                    hasAccountUnblockProgression(text)
                    && hasIncompleteActualUnblock(text)
                    && hasCompletedHelpSeeking(text)
                    && !hasActualProtectiveBlock(text)
            ) {
                return true;
            }
        }

        return false;
    }


    private boolean hasAccountUnblockProgression(
            String text
    ) {

        return containsAny(
                text,

                "계정 해제 요청",
                "계정 차단 해제 요청",
                "해제 요청의 마지막 확인",
                "해제 요청 마지막 확인",
                "해제 요청 버튼",
                "해제 신청",
                "해제 신청서"
        );
    }


    private boolean hasIncompleteActualUnblock(
            String text
    ) {

        return containsAny(
                text,

                "아직 해제되기 전에",
                "아직 해제되지 않았",
                "아직 실제로 해제되지는 않았",
                "실제로 해제되지는 않았",
                "실제로 풀리지는 않았",
                "계정도 실제로 풀리지는 않았",
                "최종 확인은 하지 않았"
        );
    }


    private boolean hasCompletedHelpSeeking(
            String text
    ) {

        return containsAny(
                text,

                "상담센터에 도움을 요청",
                "상담을 요청",
                "상담센터에 전화",
                "상담센터에 연락",
                "도움을 요청"
        );
    }


    private boolean hasActualProtectiveBlock(
            String text
    ) {

        if (containsAny(
                text,

                "차단하지 않았",
                "차단하지는 않았",
                "막지 않았",
                "막지는 않았"
        )) {
            return false;
        }

        return containsAny(
                text,

                "계정을 다시 차단했",
                "계정 다시 차단했",
                "계정을 차단했",
                "계정을 막았",
                "계정을 다시 막았",
                "계정을 막아놓",
                "사이트도 막고",
                "차단을 완료"
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
