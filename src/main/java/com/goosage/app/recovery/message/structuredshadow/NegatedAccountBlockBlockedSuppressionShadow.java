package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class NegatedAccountBlockBlockedSuppressionShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        for (StructuredEventShadow structured : events) {

            String text = structured.text();

            if (text == null || text.isBlank()) {
                continue;
            }

            if (containsAny(
                    text,
                    "계정을 다시 차단하지는 않았",
                    "계정을 다시 차단하지 않았",
                    "계정은 다시 차단하지 않았",
                    "계정을 차단하지는 않았",
                    "계정을 차단하지 않았",
                    "다시 막지는 않았",
                    "다시 막지 않았"
            )) {
                return true;
            }
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
