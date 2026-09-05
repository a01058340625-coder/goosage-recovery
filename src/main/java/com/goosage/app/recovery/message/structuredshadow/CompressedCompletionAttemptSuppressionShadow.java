package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class CompressedCompletionAttemptSuppressionShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        boolean hasCompletedWager = false;
        StringBuilder allText = new StringBuilder();

        for (StructuredEventShadow structured : events) {

            allText.append(structured.text()).append(" ");

            if (
                    "WAGER".equals(
                            String.valueOf(
                                    structured.event().actionType()
                            )
                    )
                    && "COMPLETED".equals(
                            String.valueOf(
                                    structured.event().actionStage()
                            )
                    )
            ) {
                hasCompletedWager = true;
            }
        }

        if (!hasCompletedWager) {
            return false;
        }

        String text = allText.toString();

        if (!hasCompressedCompletion(text)) {
            return false;
        }

        if (hasConcreteProgression(text)) {
            return false;
        }

        return true;
    }


    private boolean hasCompressedCompletion(String text) {
        return containsAny(
                text,
                "돈을 걸었어",
                "베팅까지 한 번 성립",
                "베팅이 성립된 뒤",
                "베팅을 해버렸어",
                "실제로 베팅을 했고"
        );
    }


    private boolean hasConcreteProgression(String text) {
        return containsAny(
                text,
                "로그인",
                "입금",
                "충전",
                "금액을 입력",
                "금액 입력",
                "베팅 버튼",
                "제출",
                "주문",
                "배당",
                "경기를 골라",
                "경기 하나를 골라",
                "결제수단"
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
