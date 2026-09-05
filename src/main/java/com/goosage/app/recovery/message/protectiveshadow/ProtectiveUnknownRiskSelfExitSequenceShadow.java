package com.goosage.app.recovery.message.protectiveshadow;

import java.util.List;

import com.goosage.app.recovery.message.structuredshadow.StructuredEventShadow;

public class ProtectiveUnknownRiskSelfExitSequenceShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {
        boolean riskSeen = false;

        for (StructuredEventShadow structured : events) {

            String text = structured.text();

            String action =
                    structured.event().actionType().name();

            String stage =
                    structured.event().actionStage().name();


            if (
                    isRiskAction(action)
                    && !stage.equals("THOUGHT")
                    && !stage.equals("UNKNOWN")
                    && !stage.equals("COMPLETED")
            ) {
                riskSeen = true;
            }


            if (hasRiskProgressEvidence(text)) {
                riskSeen = true;
            }


            if (
                    riskSeen
                    && isSelfExit(text)
            ) {
                return true;
            }
        }

        return false;
    }


    private boolean isRiskAction(String action) {
        return action.equals("SEARCH")
                || action.equals("ACCESS")
                || action.equals("LOGIN")
                || action.equals("FUNDING")
                || action.equals("WAGER");
    }


    private boolean hasRiskProgressEvidence(String text) {
        return containsAny(
                text,
                "카지노 화면",
                "앱을 열어",
                "앱을 열었",
                "배당표",
                "추가 입금 화면",
                "입금 화면",
                "로그인 화면"
        );
    }


    private boolean isSelfExit(String text) {
        return containsAny(
                text,
                "바로 껐",
                "화면을 끄",
                "브라우저를 닫",
                "사이트를 닫"
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
