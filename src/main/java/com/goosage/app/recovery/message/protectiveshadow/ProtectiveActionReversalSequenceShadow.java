package com.goosage.app.recovery.message.protectiveshadow;

import java.util.List;

import com.goosage.app.recovery.message.action.ActionStage;
import com.goosage.app.recovery.message.action.ActionType;
import com.goosage.app.recovery.message.structuredshadow.StructuredEventShadow;

public class ProtectiveActionReversalSequenceShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {
        boolean riskOpen = false;

        for (StructuredEventShadow structured : events) {

            ActionType action =
                    structured.event().actionType();

            ActionStage stage =
                    structured.event().actionStage();

            String text =
                    structured.text();

            if (
                    isRiskAction(action)
                    && (
                            (
                                    stage != ActionStage.THOUGHT
                                    && stage != ActionStage.UNKNOWN
                                    && stage != ActionStage.COMPLETED
                            )
                            || hasProgressionEvidence(text)
                    )
            ) {
                riskOpen = true;
            }

            if (
                    riskOpen
                    && isReversal(text)
            ) {
                return true;
            }
        }

        return false;
    }

    private boolean isRiskAction(ActionType action) {
        return action == ActionType.SEARCH
                || action == ActionType.ACCESS
                || action == ActionType.LOGIN
                || action == ActionType.FUNDING
                || action == ActionType.WAGER;
    }

    private boolean hasProgressionEvidence(String text) {
        return containsAny(
                text,
                "금액을 입력",
                "금액도 입력",
                "입력했",
                "제출 직전"
        );
    }

    private boolean isReversal(String text) {

        if (
                (
                        text.contains("적어놨다가")
                        || text.contains("적어 놨다가")
                        || text.contains("적어놓았다가")
                )
                && text.contains("지웠")
        ) {
            return true;
        }
        return containsAny(
                text,
                "입력했다가 지웠",
                "적었다가 지웠",
                "마음이 바뀌",
                "마음을 바꿔",
                "취소했",
                "취소하려고"
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
