package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class NonMotivatedWagerCancelBlockedSuppressionShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        for (StructuredEventShadow structured : events) {

            String text = structured.text();

            if (text == null || text.isBlank()) {
                continue;
            }

            String action = String.valueOf(
                    structured.event().actionType()
            );

            String stage = String.valueOf(
                    structured.event().actionStage()
            );

            String stopCause = String.valueOf(
                    structured.event().stopCause()
            );

            boolean wagerSubmitCancel =
                    "WAGER".equals(action)
                    && (
                        "SUBMITTED".equals(stage)
                        || text.contains("베팅 버튼을 눌렀")
                    )
                    && "SELF_STOP".equals(stopCause)
                    && containsAny(
                        text,
                        "주문이 완료되기 전에 취소",
                        "주문 완료 전에 취소",
                        "베팅이 완료되기 전에 취소"
                    )
                    && containsAny(
                        text,
                        "실제 베팅은 성립되지 않았",
                        "베팅은 성립되지 않았",
                        "실제로 성립되지는 않았"
                    );

            if (!wagerSubmitCancel) {
                continue;
            }

            if (hasProtectiveMotivation(text)) {
                continue;
            }

            return true;
        }

        return false;
    }


    private boolean hasProtectiveMotivation(
            String text
    ) {

        return containsAny(
                text,
                "위험",
                "무서",
                "찜찜",
                "불안",
                "후회",
                "정신 차",
                "그만해야",
                "도박을 끊",
                "다시는 하지",
                "문제가 될",
                "손실이 걱정",
                "중독"
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
