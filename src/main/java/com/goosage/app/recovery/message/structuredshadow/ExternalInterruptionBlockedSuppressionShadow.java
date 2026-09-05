package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class ExternalInterruptionBlockedSuppressionShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        StringBuilder allText = new StringBuilder();

        for (StructuredEventShadow structured : events) {
            allText.append(structured.text()).append(" ");
        }

        String text = allText.toString();

        boolean externalInterruption =
                containsAny(
                        text,
                        "연락이 와서",
                        "전화가 와서",
                        "통화를 하느라",
                        "전화 때문에",
                        "연락 때문에"
                );

        if (!externalInterruption) {
            return false;
        }

        boolean actionClosed =
                containsAny(
                        text,
                        "그냥 닫았",
                        "창을 닫았",
                        "페이지를 닫"
                );

        return actionClosed;
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
