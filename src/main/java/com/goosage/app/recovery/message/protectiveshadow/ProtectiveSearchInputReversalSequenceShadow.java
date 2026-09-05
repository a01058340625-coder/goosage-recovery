package com.goosage.app.recovery.message.protectiveshadow;

import java.util.List;

import com.goosage.app.recovery.message.action.ActionStage;
import com.goosage.app.recovery.message.action.ActionType;
import com.goosage.app.recovery.message.structuredshadow.StructuredEventShadow;

public class ProtectiveSearchInputReversalSequenceShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {
        for (int i = 0; i < events.size(); i++) {

            StructuredEventShadow current =
                    events.get(i);

            if (
                    current.event().actionType()
                            != ActionType.SEARCH
                    || current.event().actionStage()
                            != ActionStage.INPUT
            ) {
                continue;
            }

            if (isErase(current.text())) {
                return true;
            }

            if (i + 1 < events.size()) {
                String nextText =
                        events.get(i + 1).text();

                if (isErase(nextText)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isErase(String text) {
        return containsAny(
                text,
                "지웠",
                "결과가 뜨기 전에"
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
