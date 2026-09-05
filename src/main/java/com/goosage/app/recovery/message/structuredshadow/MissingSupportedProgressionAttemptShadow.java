package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class MissingSupportedProgressionAttemptShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        StringBuilder allText = new StringBuilder();

        for (StructuredEventShadow structured : events) {
            allText.append(structured.text()).append(" ");
        }

        String text = allText.toString();


        // ----------------------------------------------------
        // A. Gambling App Discovery
        // ID143 / ID177
        // ----------------------------------------------------

        boolean appDiscovery =
                containsAny(
                        text,
                        "앱을 찾다가",
                        "앱을 찾아서",
                        "관련 앱을 찾아서"
                )
                && containsAny(
                        text,
                        "설치는 안 하고",
                        "화면만 잠깐 봤",
                        "화면을 봤"
                );

        if (appDiscovery) {
            return true;
        }


        // ----------------------------------------------------
        // B. Concrete Wager Preparation
        // ID208
        // ----------------------------------------------------

        boolean wagerPreparation =
                containsAny(
                        text,
                        "경기 하나 보고",
                        "경기를 보고"
                )
                && containsAny(
                        text,
                        "베팅 금액도 확인",
                        "베팅 금액을 확인"
                )
                && containsAny(
                        text,
                        "실제로 돈을 걸지는 않",
                        "베팅은 하지 않"
                );

        if (wagerPreparation) {
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
