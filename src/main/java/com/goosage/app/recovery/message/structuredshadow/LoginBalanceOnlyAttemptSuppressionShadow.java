package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class LoginBalanceOnlyAttemptSuppressionShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        boolean hasLoginStarted = false;
        boolean hasDeeperProgression = false;

        StringBuilder allText = new StringBuilder();

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
                    "LOGIN".equals(action)
                    && "STARTED".equals(stage)
            ) {
                hasLoginStarted = true;
            }

            if (
                    containsAny(
                            action,
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
                hasDeeperProgression = true;
            }
        }

        if (!hasLoginStarted) {
            return false;
        }

        if (hasDeeperProgression) {
            return false;
        }

        String text = allText.toString();

        boolean balanceOnly =
                containsAny(
                        text,
                        "잔액도 확인",
                        "잔액을 확인"
                );

        boolean noFundingOrWager =
                containsAny(
                        text,
                        "돈을 새로 넣거나 베팅은 아직 안 했",
                        "입금은 안 했",
                        "베팅은 안 했",
                        "돈을 넣지 않",
                        "베팅하지 않"
                );

        return balanceOnly && noFundingOrWager;
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
