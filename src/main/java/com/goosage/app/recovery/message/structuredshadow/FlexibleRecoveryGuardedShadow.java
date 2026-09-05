package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class FlexibleRecoveryGuardedShadow {

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

            if (historical) {
                continue;
            }

            if (isCompletedRecovery(text)) {
                return true;
            }
        }

        return false;
    }


    private boolean isCompletedRecovery(String text) {

        if (
                containsAny(
                        text,
                        "고객센터",
                        "상담원",
                        "해제"
                )
        ) {
            return false;
        }

        if (
                text.contains("앱")
                && containsAny(
                        text,
                        "지웠",
                        "삭제했",
                        "삭제했습니다",
                        "삭제했고"
                )
                && !text.contains("삭제할까")
                && !text.contains("삭제할지")
                && !text.contains("앱을 다시 지웠")
        ) {
            return true;
        }

        if (
                containsAny(
                        text,
                        "누나한테",
                        "아내한테",
                        "배우자에게",
                        "가족에게"
                )
                && containsAny(
                        text,
                        "얘기했",
                        "말했",
                        "사실대로 말"
                )
        ) {
            return true;
        }

        if (
                text.contains("상담")
                && containsAny(
                        text,
                        "신청합니다",
                        "예약을 잡",
                        "예약까지 잡",
                        "일정을 잡",
                        "실제로 전화",
                        "실제로 연락"
                )
        ) {
            return true;
        }

        if (
                containsAny(
                        text,
                        "차단했습니다",
                        "차단했어요",
                        "나오지 않도록 차단"
                )
        ) {
            return true;
        }

        if (
                containsAny(
                        text,
                        "카드",
                        "결제카드",
                        "휴대폰"
                )
                && containsAny(
                        text,
                        "맡겼",
                        "맡겨",
                        "맡겨두기로",
                        "건네주었",
                        "건네주"
                )
        ) {
            return true;
        }

        if (
                text.contains("입금 수단")
                && text.contains("삭제")
        ) {
            return true;
        }

        return false;
    }


    private boolean hasHistoryStart(String text) {
        return containsAny(
                text,
                "몇 주 전",
                "몇 달 전",
                "한 달 전",
                "몇 년 전",
                "예전에"
        );
    }


    private boolean hasNewTime(String text) {
        return containsAny(
                text,
                "어제",
                "오늘",
                "다음 날",
                "다음날",
                "이번 주",
                "지난주",
                "지난 주",
                "얼마 전에",
                "며칠 전"
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
