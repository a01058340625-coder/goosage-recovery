package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class NonGamblingDesireTargetUrgeSuppressionShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        StringBuilder allText = new StringBuilder();

        for (StructuredEventShadow structured : events) {
            allText.append(structured.text()).append(" ");
        }

        String text = allText.toString();


        // ----------------------------------------------------
        // A. Account-Control Desire
        // ID42
        // ----------------------------------------------------

        boolean accountControlDesire =
                containsAny(
                        text,
                        "다시 풀고 싶은 생각",
                        "해제하고 싶은 생각",
                        "차단을 풀고 싶"
                )
                && containsAny(
                        text,
                        "차단",
                        "해제",
                        "계정"
                );

        if (accountControlDesire) {
            return true;
        }


        // ----------------------------------------------------
        // B. Site-Access Desire
        // ID120
        // ----------------------------------------------------

        boolean siteAccessDesire =
                containsAny(
                        text,
                        "다시 들어가고 싶은 생각",
                        "사이트에 들어가고 싶"
                )
                && containsAny(
                        text,
                        "사이트 주소",
                        "사이트를 다시 검색",
                        "주소를 다시 검색"
                )
                && !containsAny(
                        text,
                        "베팅하고 싶",
                        "도박하고 싶",
                        "슬롯을 하고 싶",
                        "한 번만 해보",
                        "만회",
                        "본전"
                );

        if (siteAccessDesire) {
            return true;
        }


        // ----------------------------------------------------
        // C. Generic Persistent Recall + Unclear Intent
        // ID331
        // ----------------------------------------------------

        boolean genericRecallUnclearIntent =
                containsAny(
                        text,
                        "계속 생각나서",
                        "계속 생각이 나서"
                )
                && containsAny(
                        text,
                        "뭘 하려던 건지",
                        "무엇을 하려던 건지"
                )
                && containsAny(
                        text,
                        "확실하지 않",
                        "잘 모르겠"
                );

        if (genericRecallUnclearIntent) {
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
