package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class NarrowPersistentCognitivePullUrgeShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        for (StructuredEventShadow structured : events) {

            String text = structured.text();

            if (isExcluded(text)) {
                continue;
            }

            if (isPersistentPull(text)) {
                return true;
            }
        }

        return false;
    }


    private boolean isPersistentPull(String text) {
        return containsAny(
                text,
                "도박 생각만 자꾸",
                "자꾸 도박 생각",
                "생각보다 자주 떠오르",
                "생각이 가끔 난",
                "예전 생각이 계속 났",
                "계속 생각이 나더라",
                "계속 생각은 납니다",

                /*
                 * OOS32_PERSISTENT_GAMBLING_COGNITIVE_PULL_V1
                 * Narrow persistent gambling cognition expressions.
                 */
                "\uB3C4\uBC15 \uC0DD\uAC01\uC774 \uACC4\uC18D \uB098\uC11C",
                "\uB3C4\uBC15 \uC0DD\uAC01\uC774 \uC790\uAFB8 \uB098\uC11C",
                "\uB3C4\uBC15 \uC0DD\uAC01\uC774 \uBC18\uBCF5\uD574\uC11C \uB098",
                "\uB3C4\uBC15 \uC0DD\uAC01\uC774 \uC5EC\uB7EC \uBC88 \uB5A0\uC62C",
                "\uB3C4\uBC15 \uC0DD\uAC01\uC774 \uACC4\uC18D \uB5A0\uC62C"
        );
    }


    private boolean isExcluded(String text) {
        return containsAny(
                text,
                "예전에 하던 생각이 자꾸",
                "마음에 걸렸",
                "마음에 걸린",
                "회사 생각"
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
