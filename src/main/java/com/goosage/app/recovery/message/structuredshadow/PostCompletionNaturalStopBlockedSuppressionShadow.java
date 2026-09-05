package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class PostCompletionNaturalStopBlockedSuppressionShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        int lastCompletedWagerIndex = -1;

        for (int i = 0; i < events.size(); i++) {

            StructuredEventShadow structured = events.get(i);

            String action =
                    String.valueOf(
                            structured.event().actionType()
                    );

            String stage =
                    String.valueOf(
                            structured.event().actionStage()
                    );

            if (
                    "WAGER".equals(action)
                    && "COMPLETED".equals(stage)
            ) {
                lastCompletedWagerIndex = i;
            }
        }

        if (lastCompletedWagerIndex < 0) {
            return false;
        }


        // 완료 이후 새로운 concrete risk progression이 있으면
        // natural stop suppression 금지
        for (
                int i = lastCompletedWagerIndex + 1;
                i < events.size();
                i++
        ) {

            StructuredEventShadow structured = events.get(i);

            String action =
                    String.valueOf(
                            structured.event().actionType()
                    );

            String stage =
                    String.valueOf(
                            structured.event().actionStage()
                    );

            if (
                    containsAny(
                            action,
                            "SEARCH",
                            "ACCESS",
                            "LOGIN",
                            "FUNDING",
                            "WAGER"
                    )
                    && containsAny(
                            stage,
                            "STARTED",
                            "INPUT",
                            "SUBMITTED",
                            "COMPLETED"
                    )
            ) {
                return false;
            }
        }


        StringBuilder allText = new StringBuilder();

        for (StructuredEventShadow structured : events) {
            allText.append(structured.text()).append(" ");
        }

        String text = allText.toString();

        return containsAny(
                text,
                "휴대폰을 내려놨",
                "휴대폰을 내려놓",
                "추가로 하지는 않",
                "더 하지 않",
                "그냥 멈췄"
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
