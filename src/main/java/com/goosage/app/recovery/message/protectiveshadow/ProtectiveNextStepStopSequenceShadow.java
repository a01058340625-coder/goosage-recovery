package com.goosage.app.recovery.message.protectiveshadow;

import java.util.List;

import com.goosage.app.recovery.message.action.ActionStage;
import com.goosage.app.recovery.message.action.ActionType;
import com.goosage.app.recovery.message.structuredshadow.StructuredEventShadow;

public class ProtectiveNextStepStopSequenceShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {
        boolean riskOpen = false;
        boolean candidate = false;

        for (StructuredEventShadow structured : events) {

            ActionType action =
                    structured.event().actionType();

            ActionStage stage =
                    structured.event().actionStage();

            String text =
                    structured.text();

            if (
                    action == ActionType.WAGER
                    && stage == ActionStage.COMPLETED
            ) {
                riskOpen = false;
                candidate = false;
                continue;
            }

            if (
                    isRiskAction(action)
                    && stage != ActionStage.THOUGHT
                    && stage != ActionStage.UNKNOWN
                    && stage != ActionStage.COMPLETED
            ) {
                riskOpen = true;
            }

            if (
                    riskOpen
                    && isNextStepStop(text)
            ) {
                candidate = true;
                riskOpen = false;
            }
        }

        return candidate;
    }

    private boolean isRiskAction(ActionType action) {
        return action == ActionType.SEARCH
                || action == ActionType.ACCESS
                || action == ActionType.LOGIN
                || action == ActionType.FUNDING
                || action == ActionType.WAGER;
    }

    private boolean isNextStepStop(String text) {
        return containsAny(
                text,
                "로그인까지는 안 갔",
                "로그인하지는 않았",
                "로그인하지 않",
                "더 안 눌렀",
                "그 이상은 하지 않",
                "설치 버튼은 누르지 않",
                "설치는 안 했",
                "설치까지는 하지 않",
                "더 들어가진 않",
                "베팅은 하지 않"
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
