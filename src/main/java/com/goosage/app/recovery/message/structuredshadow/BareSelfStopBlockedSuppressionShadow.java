package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class BareSelfStopBlockedSuppressionShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        StringBuilder allText = new StringBuilder();

        boolean hasFundingProgression = false;

        for (StructuredEventShadow structured : events) {

            String action =
                    String.valueOf(
                            structured.event().actionType()
                    );

            String stage =
                    String.valueOf(
                            structured.event().actionStage()
                    );

            allText.append(structured.text()).append(" ");

            if (
                    "FUNDING".equals(action)
                    && containsAny(
                            stage,
                            "STARTED",
                            "INPUT",
                            "SUBMITTED"
                    )
            ) {
                hasFundingProgression = true;
            }
        }

        if (!hasFundingProgression) {
            return false;
        }

        String text = allText.toString();

        boolean hasBareStop =
                containsAny(
                        text,
                        "그냥 나왔",
                        "그냥 컴퓨터를 껐",
                        "그냥 껐"
                );

        if (!hasBareStop) {
            return false;
        }

        boolean hasProtectiveReason =
                containsAny(
                        text,
                        "무서워",
                        "무서워져",
                        "하기 싫",
                        "안 하길 잘",
                        "약속이 생각",
                        "후회",
                        "다시 시작하면 안",
                        "멈춰야"
                );

        if (hasProtectiveReason) {
            return false;
        }

        return true;
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
