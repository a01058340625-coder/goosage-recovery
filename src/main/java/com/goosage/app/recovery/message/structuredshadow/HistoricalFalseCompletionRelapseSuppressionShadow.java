package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class HistoricalFalseCompletionRelapseSuppressionShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        StringBuilder allText = new StringBuilder();

        for (StructuredEventShadow structured : events) {
            allText.append(structured.text()).append(" ");
        }

        String text = allText.toString();


        // ----------------------------------------------------
        // A. Historical gambling completion
        // ID154 / ID492
        // ----------------------------------------------------

        boolean historicalCompletion =
                containsAny(
                        text,
                        "예전에는",
                        "예전에"
                )
                && containsAny(
                        text,
                        "돈을 자주 걸었",
                        "슬롯을 하다가"
                )
                && containsAny(
                        text,
                        "한동안 끊었",
                        "다시는 안 하겠",
                        "말한 상태"
                );

        if (historicalCompletion) {
            return true;
        }


        // ----------------------------------------------------
        // B. Login completion falsely mapped as WAGER completion
        // ID432
        // ----------------------------------------------------

        boolean falseWagerCompletion =
                containsAny(
                        text,
                        "로그인에 성공",
                        "로그인이 됐"
                )
                && containsAny(
                        text,
                        "카지노 화면이",
                        "화면이 그대로"
                )
                && containsAny(
                        text,
                        "입금 메뉴까지",
                        "금액을 적어"
                )
                && containsAny(
                        text,
                        "결제수단을 선택하기 전",
                        "베팅하지"
                );

        if (falseWagerCompletion) {
            return true;
        }

        return false;
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
