package com.goosage.app.recovery.message.protectiveshadow;

import java.util.List;

import com.goosage.app.recovery.message.structuredshadow.StructuredEventShadow;

public class ProtectiveMotivatedNextStepStopSequenceShadow {

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
                    action.equals("SEARCH")
                    && stage.equals("STARTED")
            ) {
                riskSeen = true;
            }


            if (!riskSeen) {
                continue;
            }


            boolean loginScreen =
                    text.contains("로그인 화면");

            boolean motivatedStop =
                    text.contains("약속")
                    && text.contains("로그인")
                    && text.contains("하지 않");


            if (
                    loginScreen
                    && motivatedStop
            ) {
                return true;
            }


            if (
                    (
                        text.contains("휴대폰을 내려놓")
                        || text.contains("산책")
                    )
                    && hasProtectiveContext(events)
            ) {
                return true;
            }
        }

        return false;
    }


    private boolean hasProtectiveContext(
            List<StructuredEventShadow> events
    ) {
        boolean promise = false;
        boolean loginScreen = false;
        boolean loginNotDone = false;

        for (StructuredEventShadow structured : events) {

            String text = structured.text();

            if (text.contains("약속")) {
                promise = true;
            }

            if (text.contains("로그인 화면")) {
                loginScreen = true;
            }

            if (
                    text.contains("로그인")
                    && text.contains("하지 않")
            ) {
                loginNotDone = true;
            }
        }

        return promise
                && loginScreen
                && loginNotDone;
    }
}
