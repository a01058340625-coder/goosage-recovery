package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class FailedWagerSubmitAttemptShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        for (StructuredEventShadow structured : events) {

            String text =
                    structured.text();

            if (text == null || text.isBlank()) {
                continue;
            }

            if (isPreSubmitOnly(text)) {
                continue;
            }

            if (hasFailedWagerSubmit(text)) {
                return true;
            }
        }

        return false;
    }


    private boolean hasFailedWagerSubmit(
            String text
    ) {

        boolean submitExecuted =
                containsAny(
                        text,
                        "베팅 버튼을 눌렀",
                        "베팅 버튼은 눌렀",
                        "베팅 주문을 눌렀",
                        "주문 버튼을 눌렀",
                        "베팅을 제출했",
                        "주문을 제출했"
                );

        if (!submitExecuted) {
            return false;
        }

        boolean failureAfterSubmit =
                containsAny(
                        text,
                        "주문이 실패",
                        "주문 실패",
                        "베팅이 실패",
                        "베팅은 성립되지 않았",
                        "베팅이 성립되지 않았",
                        "주문이 처리되지 않았",
                        "서버 오류",
                        "인증 오류"
                );

        return failureAfterSubmit;
    }


    private boolean isPreSubmitOnly(
            String text
    ) {

        return containsAny(
                text,
                "베팅 화면만",
                "경기만 선택",
                "베팅 금액만 입력",
                "금액까지 입력했지만 주문 버튼은 누르지",
                "베팅 버튼을 누르기 직전",
                "주문 버튼을 누르기 전",
                "최종 확인 전에 취소"
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
